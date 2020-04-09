package com.example.server.keystore;

import com.example.server.Model.CertificateModel;
import com.example.server.Repository.CertificateRepository;
import com.example.server.data.IssuerData;
import com.example.server.dto.CertificateDTO;
import com.example.server.dto.CertificateExchangeDTO;
import com.example.server.enumeration.KeyUsages;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;


public class KeyStoreReader {

    private KeyStore keyStore;

    public KeyStoreReader() {
        try {
            keyStore = KeyStore.getInstance("PKCS12");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }

    public IssuerData readIssuerFromStore(String keyStoreFile, String alias, char[] password, char[] keyPass) {
        try {
            //Datoteka se ucitava
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
            keyStore.load(in, password);
            //Iscitava se sertifikat koji ima dati alias
            Certificate cert = keyStore.getCertificate(alias);
            //Iscitava se privatni kljuc vezan za javni kljuc koji se nalazi na sertifikatu sa datim aliasom
            PrivateKey privKey = (PrivateKey) keyStore.getKey(alias, keyPass);

            X500Name issuerName = new JcaX509CertificateHolder((X509Certificate) cert).getSubject();
            return new IssuerData(privKey, issuerName);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Ucitava sertifikat is KS fajla
     */
    public Certificate readCertificate(String keyStoreFile, String keyStorePass, String alias) {
        try {
            //kreiramo instancu KeyStore
            KeyStore ks = KeyStore.getInstance("PKCS12");
            //ucitavamo podatke
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
            ks.load(in, keyStorePass.toCharArray());

            if(ks.isKeyEntry(alias)) {
                Certificate cert = ks.getCertificate(alias);
                return cert;
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Ucitava privatni kljuc is KS fajla
     */
    public PrivateKey readPrivateKey(String keyStoreFile, String keyStorePass, String alias, String pass) {
        try {
            //kreiramo instancu KeyStore
            KeyStore ks = KeyStore.getInstance("PKCS12");
            //ucitavamo podatke
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
            ks.load(in, keyStorePass.toCharArray());

            if(ks.isKeyEntry(alias)) {
                PrivateKey pk = (PrivateKey) ks.getKey(alias, pass.toCharArray());
                return pk;
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public KeyStore getKeyStore(String keyStoreFile, String passString) {
        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
            char[] password = passString.toCharArray();
            try {
                ks.load(in, password);
                return ks;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ks;
        } catch (FileNotFoundException | KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<CertificateExchangeDTO> findCACerts(KeyStore ks) throws KeyStoreException, CertificateEncodingException {
        ArrayList<CertificateExchangeDTO> certificateDTOList = new ArrayList<>();
        Enumeration<String> aliases = ks.aliases();
        while(aliases.hasMoreElements()) {
            String entry = aliases.nextElement();
            System.out.println(entry);
            X509Certificate cert = (X509Certificate) ks.getCertificate(entry);

            System.out.println(cert.getIssuerX500Principal().getName());
            if(cert.getKeyUsage()[5]) {
                X500Name x500name = new JcaX509CertificateHolder(cert).getSubject();
                RDN cn = x500name.getRDNs(BCStyle.CN)[0];
                RDN org = x500name.getRDNs(BCStyle.O)[0];
                RDN email = x500name.getRDNs(BCStyle.E)[0];

                X500Name x500nameIssuer = new JcaX509CertificateHolder(cert).getIssuer();
                RDN cnIssuer = x500nameIssuer.getRDNs(BCStyle.CN)[0];
                certificateDTOList.add(new CertificateExchangeDTO(IETFUtils.valueToString(cn.getFirst().getValue()), IETFUtils.valueToString(org.getFirst().getValue()), IETFUtils.valueToString(email.getFirst().getValue()), IETFUtils.valueToString(cnIssuer.getFirst().getValue()), cert.getSerialNumber(), cert.getNotBefore(), cert.getNotAfter()));
            }
        }
        return certificateDTOList;
    }

    public Certificate readCertificateBySerialNumber(String keyStoreFile, String keyStorePass, String serialNumber) {
        try {
            //kreiramo instancu KeyStore
            KeyStore ks = KeyStore.getInstance("PKCS12");
            //ucitavamo podatke
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
            ks.load(in, keyStorePass.toCharArray());
            Enumeration<String> aliases = ks.aliases();
            while(aliases.hasMoreElements()) {
                String entry = aliases.nextElement();
                X509Certificate cert = (X509Certificate) ks.getCertificate(entry);
                if(cert.getSerialNumber().toString().equals(serialNumber)) {
                    return ks.getCertificate(entry);
                }
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PrivateKey getPrivateKey(String filePath, String alias, String password) {
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(filePath));
            keyStore.load(in, password.toCharArray());
            if(keyStore.isKeyEntry(alias)) {
                return (PrivateKey) keyStore.getKey(alias, password.toCharArray());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
//
//    KeyUsage ::= BIT STRING {
//                digitalSignature        (0),
//                nonRepudiation          (1),
//                keyEncipherment         (2),
//                dataEncipherment        (3),
//                keyAgreement            (4),
//                keyCertSign             (5),  --> true ONLY for CAs
//                cRLSign                 (6),
//                encipherOnly            (7),
//                decipherOnly            (8) }
    public ArrayList<Certificate> readAllCertificates(String filePath, String password) {
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(filePath));
            keyStore.load(in, password.toCharArray());
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
    public ArrayList<CertificateExchangeDTO> findAllCerts(KeyStore ks) throws KeyStoreException, CertificateEncodingException {
        ArrayList<CertificateExchangeDTO> certificateDTOList = new ArrayList<>();
        Enumeration<String> aliases = ks.aliases();
        while(aliases.hasMoreElements()) {
            String entry = aliases.nextElement();
            System.out.println(entry);
            X509Certificate cert = (X509Certificate) ks.getCertificate(entry);

            System.out.println(cert.getIssuerX500Principal().getName());
            X500Name x500name = new JcaX509CertificateHolder(cert).getSubject();
            RDN cn = x500name.getRDNs(BCStyle.CN)[0];
            RDN org = x500name.getRDNs(BCStyle.O)[0];
            RDN email = x500name.getRDNs(BCStyle.E)[0];

            X500Name x500nameIssuer = new JcaX509CertificateHolder(cert).getIssuer();
            RDN cnIssuer = x500nameIssuer.getRDNs(BCStyle.CN)[0];
            certificateDTOList.add(new CertificateExchangeDTO(IETFUtils.valueToString(cn.getFirst().getValue()), IETFUtils.valueToString(org.getFirst().getValue()), IETFUtils.valueToString(email.getFirst().getValue()), IETFUtils.valueToString(cnIssuer.getFirst().getValue()), cert.getSerialNumber(), cert.getNotBefore(), cert.getNotAfter()));

        }
        return certificateDTOList;
    }
}
