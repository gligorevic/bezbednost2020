package com.example.server.service;

import com.example.server.Model.CertificateModel;
import com.example.server.Repository.CertificateRepository;
import com.example.server.certificates.CertificateGenerator;
import com.example.server.certificates.Constants;
import com.example.server.dto.CertificateExchangeDTO;
import com.example.server.enumeration.KeyUsages;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;


@Service
public class KeyStoreService {

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private CertificateService certificateService;

    public KeyStore getKeyStore(String keyStoreFile, String passString) {
        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            char[] password = passString.toCharArray();

            if(keyStoreFile != null) {
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
                ks.load(in, password);
            } else {
                ks.load(null, password);
            }

            return ks;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public ArrayList<CertificateExchangeDTO> findAllValidCerts(String[] filePath, String password) throws KeyStoreException, CertificateEncodingException {
        List<KeyStore> keyStores = new ArrayList<>();
        for(String s: filePath){
            keyStores.add(getKeyStore(s, password));
        }


        ArrayList<CertificateExchangeDTO> certificateDTOList = new ArrayList<>();
        for(KeyStore ks : keyStores) {
            Enumeration<String> aliases = ks.aliases();
            while (aliases.hasMoreElements()) {
                String entry = aliases.nextElement();
                X509Certificate cert = (X509Certificate) ks.getCertificate(entry);


                X500Name x500name = new JcaX509CertificateHolder(cert).getSubject();
                RDN cn = x500name.getRDNs(BCStyle.CN)[0];
                RDN org = x500name.getRDNs(BCStyle.O)[0];
                RDN email = x500name.getRDNs(BCStyle.E)[0];
                X500Name x500nameIssuer = new JcaX509CertificateHolder(cert).getIssuer();
                RDN cnIssuer = x500nameIssuer.getRDNs(BCStyle.CN)[0];

                if (validateChain(ks.getCertificateChain(entry))) {
                    certificateDTOList.add(new CertificateExchangeDTO(rdnToString(cn), rdnToString(org), rdnToString(email), rdnToString(cnIssuer), cert.getSerialNumber(), cert.getNotBefore(), cert.getNotAfter()));
                }
            }
        }
        return certificateDTOList;
    }

    private String rdnToString(RDN rdn) {
        return IETFUtils.valueToString(rdn.getFirst().getValue());
    }

    public boolean validateChain(Certificate[] certificateChain) {
        try{
            int n = certificateChain.length;
            System.out.println("Chain length " + n);
            for (int i = 0; i < n - 1; i++) {
                X509Certificate cert = (X509Certificate)certificateChain[i];
                X509Certificate issuer = (X509Certificate)certificateChain[i + 1];

                System.out.println(cert.getIssuerX500Principal());
                System.out.println(issuer.getSubjectX500Principal());
                if (cert.getIssuerX500Principal().equals(issuer.getSubjectX500Principal()) == false) {
                    throw new Exception("Certificates do not chain");
                }
                if(certificateRepository.getBySerialNumber(cert.getSerialNumber().toString()) != null) {
                    return false;
                }

                cert.verify(issuer.getPublicKey());
            }
            X509Certificate last = (X509Certificate)certificateChain[n - 1];

            System.out.println("------------------------------");
            System.out.println(last.getIssuerX500Principal());
            System.out.println(last.getSubjectX500Principal());
            System.out.println("------------------------------");

            if (last.getIssuerX500Principal().equals(last.getSubjectX500Principal())) {
                if(certificateRepository.getBySerialNumber(last.getSerialNumber().toString()) != null) {
                    return false;
                }
                last.verify(last.getPublicKey());
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void write(KeyStore keyStore, String alias, PrivateKey privateKey, char[] password, Certificate certificate, X509Certificate issuer) {
        try {
            if(issuer == null) {
                keyStore.setKeyEntry(((X509Certificate)certificate).getSerialNumber() + "*" + alias, privateKey, password, new Certificate[] {certificate});
            } else {
                KeyStore ks = getKeyStoreBySerialNumber(issuer.getSerialNumber().toString());
                Certificate[] certificates = getCertificateChain(issuer.getSerialNumber().toString(), certificate, ks);
                for(Certificate c : certificates) {
                    System.out.println(((X509Certificate)c).getSubjectX500Principal());
                }
                keyStore.setKeyEntry(((X509Certificate)certificate).getSerialNumber() + "*" + alias, privateKey, password, certificates);
            }

        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }

    public Certificate[] getCertificateChain(String serialNumber, Certificate certificate, KeyStore keyStore){
        try {
            Enumeration<String> aliases = keyStore.aliases();
            while(aliases.hasMoreElements()) {
                String entry = aliases.nextElement();
                X509Certificate cert = (X509Certificate) keyStore.getCertificate(entry);
                if(cert.getSerialNumber().toString().equals(serialNumber)) {
                    List<Certificate> certificateList = new ArrayList<>(Arrays.asList((keyStore.getCertificateChain(entry))));
                    certificateList.add(0, certificate);

                    Certificate[] certificateChain = new Certificate[certificateList.size()];
                    for (int i = 0; i < certificateList.size(); i++) {
                        certificateChain[i] = certificateList.get(i);
                    }

                    if(!validateChain(certificateChain)) {
                        throw new Exception("Chain is not valid");
                    }

                    return certificateChain;
                }
            }
            return null;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveKeyStore(KeyStore keyStore, String fileName, char[] password) {
        try {
            keyStore.store(new FileOutputStream(fileName), password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ------------------------------------------ READER ---------------------------------------------------

    public Certificate readCertificate( String alias) {
        try {
            List<KeyStore> keyStores = new ArrayList<>();
            keyStores.add(getKeyStore(Constants.keystoreFilePathRoot, Constants.password));
            keyStores.add(getKeyStore(Constants.keystoreFilePathCA, Constants.password));
            keyStores.add(getKeyStore(Constants.keystoreFilePathEnd, Constants.password));
            for(KeyStore ks : keyStores) {
                if (ks.isKeyEntry(alias)) {
                    Certificate cert = ks.getCertificate(alias);
                    return cert;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public ArrayList<Certificate> findAllCertificates(KeyStore keyStore) {
        try {

            Enumeration<String> aliases = keyStore.aliases();
            ArrayList<Certificate> ret = new ArrayList<>();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                if (keyStore.isKeyEntry(alias)) {
                    Certificate cert = keyStore.getCertificate(alias);
                    ret.add(cert);
                }
            }
            return ret;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<CertificateExchangeDTO> findAllRevocatedCerts(String[] filePath, String password) throws KeyStoreException, CertificateEncodingException {
        List<KeyStore> keyStores = new ArrayList<>();
        for(String s: filePath){
            keyStores.add(getKeyStore(s, password));
        }

        ArrayList<CertificateExchangeDTO> certificateDTOList = new ArrayList<>();
        for(KeyStore ks : keyStores) {
            Enumeration<String> aliases = ks.aliases();
            while (aliases.hasMoreElements()) {
                String entry = aliases.nextElement();
                X509Certificate cert = (X509Certificate) ks.getCertificate(entry);


                X500Name x500name = new JcaX509CertificateHolder(cert).getSubject();
                RDN cn = x500name.getRDNs(BCStyle.CN)[0];
                RDN org = x500name.getRDNs(BCStyle.O)[0];
                RDN email = x500name.getRDNs(BCStyle.E)[0];
                X500Name x500nameIssuer = new JcaX509CertificateHolder(cert).getIssuer();
                RDN cnIssuer = x500nameIssuer.getRDNs(BCStyle.CN)[0];

                if (!validateChain(ks.getCertificateChain(entry))) {

                    CertificateExchangeDTO certDto = new CertificateExchangeDTO(rdnToString(cn), rdnToString(org), rdnToString(email), rdnToString(cnIssuer), cert.getSerialNumber(), cert.getNotBefore(), cert.getNotAfter());
                    CertificateModel certModel = certificateRepository.getBySerialNumber(cert.getSerialNumber().toString());

                    if (certModel != null) {
                        certDto.setReason(certModel.getRevokeReason());
                    } else {
                        certDto.setReason("An issuer has been revoked");
                    }

                    certificateDTOList.add(certDto);
                }
            }
        }
        return certificateDTOList;
    }



    public Certificate readCertificateBySerialNumber(String serialNumber) {
        try {
            List<KeyStore> keyStores = new ArrayList<>();
            keyStores.add(getKeyStore(Constants.keystoreFilePathRoot, Constants.password));
            keyStores.add(getKeyStore(Constants.keystoreFilePathCA, Constants.password));
            keyStores.add(getKeyStore(Constants.keystoreFilePathEnd, Constants.password));
            for(KeyStore ks : keyStores) {
                Enumeration<String> aliases = ks.aliases();
                while (aliases.hasMoreElements()) {
                    String entry = aliases.nextElement();
                    X509Certificate cert = (X509Certificate) ks.getCertificate(entry);
                    if (cert.getSerialNumber().toString().equals(serialNumber)) {
                        return ks.getCertificate(entry);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public PrivateKey getPrivateKey(String alias, String password) {
        try {
            List<KeyStore> keyStores = new ArrayList<>();
            keyStores.add(getKeyStore(Constants.keystoreFilePathRoot, Constants.password));
            keyStores.add(getKeyStore(Constants.keystoreFilePathCA, Constants.password));
            keyStores.add(getKeyStore(Constants.keystoreFilePathEnd, Constants.password));
            for(KeyStore keyStore: keyStores) {
                if (keyStore.isKeyEntry(alias)) {
                    return (PrivateKey) keyStore.getKey(alias, password.toCharArray());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<CertificateExchangeDTO> findCACerts(KeyUsages[] keyUsages) throws Exception {
        List<KeyStore> keyStores = new ArrayList<>();
        keyStores.add(getKeyStore(Constants.keystoreFilePathRoot, Constants.password));
        keyStores.add(getKeyStore(Constants.keystoreFilePathCA, Constants.password));
        ArrayList<CertificateExchangeDTO> certificateDTOList = new ArrayList<>();
        for(KeyStore ks :keyStores) {
            Enumeration<String> aliases = ks.aliases();
            while (aliases.hasMoreElements()) {
                String entry = aliases.nextElement();
                X509Certificate cert = (X509Certificate) ks.getCertificate(entry);
                boolean flag = true;

                for(boolean b : cert.getKeyUsage()){
                    System.out.println(b);
                }
                System.out.println();
                for(KeyUsages keyUsage: keyUsages){
                    System.out.println(KeyUsages.valueOf(keyUsage.toString()).ordinal());
                    if(!cert.getKeyUsage()[KeyUsages.valueOf(keyUsage.toString()).ordinal()]){
                        flag = false;
                        break;
                    }
                }
                //budz- proveriti da li je dovoljno da se proveri samo keyUsage[5] ili bi trebalo jos nesto da se proveri
                if (cert.getKeyUsage()[5] && certificateService.checkPrivateKeyDuration(cert) && validateChain(ks.getCertificateChain(entry)) && flag) {
                    X500Name x500name = new JcaX509CertificateHolder(cert).getSubject();
                    RDN cn = x500name.getRDNs(BCStyle.CN)[0];
                    RDN org = x500name.getRDNs(BCStyle.O)[0];
                    RDN email = x500name.getRDNs(BCStyle.E)[0];

                    X500Name x500nameIssuer = new JcaX509CertificateHolder(cert).getIssuer();
                    RDN cnIssuer = x500nameIssuer.getRDNs(BCStyle.CN)[0];
                    certificateDTOList.add(new CertificateExchangeDTO(IETFUtils.valueToString(cn.getFirst().getValue()), IETFUtils.valueToString(org.getFirst().getValue()), IETFUtils.valueToString(email.getFirst().getValue()), IETFUtils.valueToString(cnIssuer.getFirst().getValue()), cert.getSerialNumber(), cert.getNotBefore(), cert.getNotAfter()));
                }
            }
        }
        return certificateDTOList;
    }

    public KeyStore getKeyStoreBySerialNumber(String serialNumber) throws KeyStoreException {
        List<KeyStore> keyStores = new ArrayList<>();
        keyStores.add(getKeyStore(Constants.keystoreFilePathRoot, Constants.password));
        keyStores.add(getKeyStore(Constants.keystoreFilePathCA, Constants.password));
        keyStores.add(getKeyStore(Constants.keystoreFilePathEnd, Constants.password));
        for(KeyStore ks : keyStores) {
            Enumeration<String> aliases = ks.aliases();
            while (aliases.hasMoreElements()) {
                String entry = aliases.nextElement();
                X509Certificate cert = (X509Certificate) ks.getCertificate(entry);
                if (cert.getSerialNumber().toString().equals(serialNumber)) {
                    return ks;
                }
            }
        }
        return null;
    }

}
