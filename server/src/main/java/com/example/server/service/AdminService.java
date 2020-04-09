package com.example.server.service;

import com.example.server.Model.CertificateModel;
import com.example.server.Repository.CertificateRepository;
import com.example.server.certificates.CertificateGenerator;
import com.example.server.certificates.Constants;
import com.example.server.data.IssuerData;
import com.example.server.data.SubjectData;
import com.example.server.dto.CertificateDTO;
import com.example.server.dto.CertificateExchangeDTO;
import com.example.server.keystore.KeyStoreReader;
import com.example.server.keystore.KeyStoreWriter;
import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.*;
import java.util.ArrayList;

@Service
public class AdminService {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private CertificateRepository certificateRepository;

    private KeyStoreReader keyStoreReader = new KeyStoreReader();
    private KeyStoreWriter keyStoreWriter = new KeyStoreWriter();

    public CertificateDTO createCertificate(CertificateDTO certificateDTO) {
        try {
            keyStoreWriter.loadKeyStore(Constants.keystoreFilePath, Constants.password.toCharArray());

            SubjectData subjectData = CertificateGenerator.generateSubjectData(certificateDTO.getCommonName(),certificateDTO.getOrganization(), certificateDTO.getOrganizationalUnit(), certificateDTO.getCity(), certificateDTO.getMail(), certificateDTO.getNotBefore(), certificateDTO.getNotAfter());
            KeyPair keyPair = CertificateGenerator.generateKeyPair();

            X509Certificate issuerCert = (X509Certificate) keyStoreReader.readCertificateBySerialNumber(Constants.keystoreFilePath, Constants.password, certificateDTO.getIssuer());

            BigInteger issuerCertSN = issuerCert.getSerialNumber();

            X500Name x500name = new JcaX509CertificateHolder(issuerCert).getSubject();
            RDN cn = x500name.getRDNs(BCStyle.CN)[0];
            RDN org = x500name.getRDNs(BCStyle.O)[0];
            RDN email = x500name.getRDNs(BCStyle.E)[0];
            RDN ou = x500name.getRDNs(BCStyle.OU)[0];
            RDN city = x500name.getRDNs(BCStyle.C)[0];

            IssuerData issuerData = CertificateGenerator.generateIssuerData(rdnToString(cn), rdnToString(org),rdnToString(ou),rdnToString(city),rdnToString(email), keyStoreReader.getPrivateKey(Constants.keystoreFilePath, rdnToString(cn), Constants.password));

            String issuerAlias = rdnToString(cn);
            //provera da li se vreme validnosti sertifikata nalazi u okviru vremena validnosti issuer-a
            if(certificateDTO.getNotAfter().after(issuerCert.getNotAfter()) || certificateDTO.getNotBefore().before(issuerCert.getNotBefore())){
                return null;
            }
            CertificateGenerator cg = new CertificateGenerator();
            X509Certificate cert = cg.generateCertificate(subjectData, issuerData, issuerCertSN, issuerAlias, certificateDTO.getKeyUsages());

            keyStoreWriter.write(certificateDTO.getCommonName(), keyPair.getPrivate(), Constants.password.toCharArray(), cert, issuerCert);
            keyStoreWriter.saveKeyStore(Constants.keystoreFilePath, Constants.password.toCharArray());

            Certificate certificate = keyStoreReader.readCertificate(Constants.keystoreFilePath, Constants.password, certificateDTO.getCommonName());
            X509Certificate c = (X509Certificate) certificate;

            System.out.println("Issuer\n");
            System.out.println(c.getIssuerDN().getName());

            System.out.println("Subject\n");
            System.out.println(c.getSubjectX500Principal().getName());
            System.out.println(c.getNotAfter());
            System.out.println(c.getNotBefore());
        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String rdnToString(RDN rdn) {
        return IETFUtils.valueToString(rdn.getFirst().getValue());
    }

    public ArrayList<CertificateExchangeDTO> getCACerts() {
        try {
            KeyStore keystore = keyStoreReader.getKeyStore(Constants.keystoreFilePath, Constants.password);

            return keyStoreReader.findCACerts(keystore);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    @Scheduled(cron = "0 0 0 * * *")
    public ArrayList<CertificateExchangeDTO> getAllCerts(){
        try{
            ArrayList<CertificateExchangeDTO> retList = certificateService.certificateCheckDate(keyStoreReader.findAllCerts((keyStoreReader.getKeyStore(Constants.keystoreFilePath, Constants.password))));
            return retList;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public CertificateExchangeDTO downloadCertificate(CertificateExchangeDTO certificateExchangeDTO){
        try{
            System.out.println(certificateExchangeDTO.getName());

            Certificate certificate = keyStoreReader.readCertificate(Constants.keystoreFilePath, Constants.password, certificateExchangeDTO.getName());

            String path = System.getProperty("user.home") + "/Downloads/";

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
            Certificate certificate = keyStoreReader.readCertificate(Constants.keystoreFilePath, Constants.password, certificateExchangeDTO.getName());
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
}
