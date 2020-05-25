package com.example.server.service;

import com.example.server.Model.CertificateModel;
import com.example.server.certificates.CertificateGenerator;
import com.example.server.certificates.Constants;
import com.example.server.data.IssuerData;
import com.example.server.data.SubjectData;
import com.example.server.dto.CertificateDTO;
import com.example.server.dto.CertificateExchangeDTO;
import com.example.server.enumeration.KeyUsages;
import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;

@Service
public class AdminService {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private KeyStoreService keyStoreService;


    //Checked  Dodati opciono generisanje vise root sertifikata
    public CertificateDTO createCertificate(CertificateDTO certificateDTO) throws Exception {

        KeyStore keyStore;

        KeyPair keyPair = CertificateGenerator.generateKeyPair();
        SubjectData subjectData = CertificateGenerator.generateSubjectData(certificateDTO.getCommonName(),certificateDTO.getOrganization(), certificateDTO.getOrganizationalUnit(), certificateDTO.getCity(), certificateDTO.getMail(), certificateDTO.getNotBefore(), certificateDTO.getNotAfter(), keyPair);

        X509Certificate issuerCert;

        if(certificateDTO.getIssuer() == null){
            keyStore = keyStoreService.getKeyStore(Constants.keystoreFilePathRoot, Constants.password);
            System.out.println("Pravi se root sertifikat");
            IssuerData issuerData = CertificateGenerator.generateIssuerData(certificateDTO.getCommonName(),certificateDTO.getOrganization(), certificateDTO.getOrganizationalUnit(), certificateDTO.getCity(), certificateDTO.getMail(), keyPair.getPrivate(), IETFUtils.valueToString(subjectData.getX500name().getRDNs(BCStyle.UID)[0].getFirst().getValue()));

            CertificateGenerator cg = new CertificateGenerator();
            X509Certificate cert = cg.generateCertificate(subjectData, issuerData, BigInteger.valueOf(keyStore.size()), new KeyUsages[]{KeyUsages.KEY_CERT_SIGN, KeyUsages.CRL_SIGN }, certificateDTO.getNotBefore(), certificateDTO.getNotAfter());

            keyStoreService.write(keyStore, certificateDTO.getCommonName(), keyPair.getPrivate(), Constants.password.toCharArray(), cert, null);
            keyStoreService.saveKeyStore(keyStore, Constants.keystoreFilePathRoot, Constants.password.toCharArray());

            Certificate certificate = keyStoreService.readCertificate(cert.getSerialNumber() + "*" +certificateDTO.getCommonName());
            X509Certificate c = (X509Certificate) certificate;

            System.out.println("================================================");
            System.out.println("Napravljen root" + keyStore.size());
            System.out.println("Issuer\n");
            System.out.println(cert.getIssuerDN().getName());

            System.out.println("Subject\n");
            System.out.println(c.getSubjectX500Principal().getName());
            System.out.println("===============================================");
            return certificateDTO;
        }else{
            issuerCert = (X509Certificate) keyStoreService.readCertificateBySerialNumber(certificateDTO.getIssuer());

            if(!certificateService.checkPrivateKeyDuration(issuerCert)) {
                throw new Exception("Issuer private key is not active");
            }
        }

        BigInteger issuerCertSN = issuerCert.getSerialNumber();

        X500Name x500name = new JcaX509CertificateHolder(issuerCert).getSubject();
        RDN cn = x500name.getRDNs(BCStyle.CN)[0];
        RDN org = x500name.getRDNs(BCStyle.O)[0];
        RDN email = x500name.getRDNs(BCStyle.E)[0];
        RDN ou = x500name.getRDNs(BCStyle.OU)[0];
        RDN city = x500name.getRDNs(BCStyle.C)[0];
        RDN uid = x500name.getRDNs(BCStyle.UID)[0];

        IssuerData issuerData = CertificateGenerator.generateIssuerData(rdnToString(cn), rdnToString(org),rdnToString(ou),rdnToString(city),rdnToString(email), keyStoreService.getPrivateKey(issuerCert.getSerialNumber() + "*" + rdnToString(cn), Constants.password), rdnToString(uid));

        //provera da li se vreme validnosti sertifikata nalazi u okviru vremena validnosti issuer-a
        if(certificateDTO.getNotAfter().after(issuerCert.getNotAfter()) || certificateDTO.getNotBefore().before(issuerCert.getNotBefore()) || certificateDTO.getNotAfter().before(new Date())){
            throw new Exception("Invalid date");
        }
        CertificateGenerator cg = new CertificateGenerator();
        X509Certificate cert = cg.generateCertificate(subjectData, issuerData, issuerCertSN, certificateDTO.getKeyUsages(), certificateDTO.getNotBefore(), certificateDTO.getNotAfter());
        String path = "";
        //budz- proveriti da li je dovoljno da se proveri samo keyUsage[5] ili bi trebalo jos nesto da se proveri
        if(cert.getKeyUsage()[5]){
            path = Constants.keystoreFilePathCA;
        }else {
            path = Constants.keystoreFilePathEnd;
        }
        keyStore = keyStoreService.getKeyStore(path, Constants.password);
        keyStoreService.write(keyStore, certificateDTO.getCommonName(), keyPair.getPrivate(), Constants.password.toCharArray(), cert, issuerCert);
        keyStoreService.saveKeyStore(keyStore, path, Constants.password.toCharArray());

        Certificate certificate = keyStoreService.readCertificate(cert.getSerialNumber() + "*" +certificateDTO.getCommonName());
        X509Certificate c = (X509Certificate) certificate;

        System.out.println("Issuer\n");
        System.out.println(c.getIssuerDN().getName());

        System.out.println("Subject\n");
        System.out.println(c.getSubjectX500Principal().getName());
        System.out.println(c.getNotAfter());
        System.out.println(c.getNotBefore());

        return certificateDTO;
    }

    private String rdnToString(RDN rdn) {
        return IETFUtils.valueToString(rdn.getFirst().getValue());
    }

    public ArrayList<CertificateExchangeDTO> getCACerts(KeyUsages[] keyUsages) {
        try {
            return keyStoreService.findCACerts(keyUsages);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //Checked
    @Scheduled(cron = "0 0 0 * * *")
    public ArrayList<CertificateExchangeDTO> getAllCerts(){
        try{
            String[] paths = {Constants.keystoreFilePathRoot, Constants.keystoreFilePathCA, Constants.keystoreFilePathEnd};
            ArrayList<CertificateExchangeDTO> retList = certificateService.certificateCheckDate(keyStoreService.findAllValidCerts(paths, Constants.password));
            return retList;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<CertificateExchangeDTO> getAllRevocatedCerts() {
        try{
            String[] paths = {Constants.keystoreFilePathRoot, Constants.keystoreFilePathCA, Constants.keystoreFilePathEnd};
            ArrayList<CertificateExchangeDTO> retList = keyStoreService.findAllRevocatedCerts(paths, Constants.password);
            return retList;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public CertificateExchangeDTO downloadCertificate(CertificateExchangeDTO certificateExchangeDTO){
        try{
            System.out.println(certificateExchangeDTO.getName());
            Certificate certificate = keyStoreService.readCertificate(certificateExchangeDTO.getSerialNumber() + "*" + certificateExchangeDTO.getName());
            String path = System.getProperty("user.home") + File.separator + "Downloads" + File.separator;

            FileOutputStream os = new FileOutputStream(path + certificateExchangeDTO.getName() + ".cer");
            os.write(Base64.encodeBase64(certificate.getEncoded(), true));
            os.close();
            return certificateExchangeDTO;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public CertificateExchangeDTO revokeCertificate(CertificateExchangeDTO certificateExchangeDTO, String reason) {
        try{
            Certificate certificate = keyStoreService.readCertificate( certificateExchangeDTO.getSerialNumber() + "*" + certificateExchangeDTO.getName());
            X509Certificate x509Certificate = (X509Certificate) certificate;
            CertificateModel certificateModel = certificateService.revokeCertificate(x509Certificate, reason);

            if(certificateModel != null){
                return certificateExchangeDTO;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public CertificateExchangeDTO checkValidity(CertificateExchangeDTO certificateExchangeDTO){
        try{
            System.out.println(certificateExchangeDTO.getName());
            Certificate certificate = keyStoreService.readCertificate(certificateExchangeDTO.getSerialNumber() + "*" + certificateExchangeDTO.getName());
            KeyStore ks = keyStoreService.getKeyStoreBySerialNumber(String.valueOf(certificateExchangeDTO.getSerialNumber()));
            if(keyStoreService.validateChain(ks.getCertificateChain(certificateExchangeDTO.getSerialNumber() + "*" + certificateExchangeDTO.getName()))){
                return certificateExchangeDTO;
            }
            return certificateExchangeDTO;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
