package com.example.server.configuration;

import com.example.server.certificates.CertificateGenerator;
import com.example.server.certificates.Constants;
import com.example.server.data.IssuerData;
import com.example.server.data.SubjectData;
import com.example.server.enumeration.KeyUsages;
import com.example.server.service.KeyStoreService;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.io.File;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;

@Component
public class AppRunner implements ApplicationRunner {

    @Autowired
    private KeyStoreService keyStoreService;

    public AppRunner() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Override
    public void run(ApplicationArguments args) {

        File f = new File(Constants.keystoreFilePath);

        if(!f.exists()) {
            System.out.println("Ne postoji keystore");
            KeyStore keyStore = keyStoreService.getKeyStore(null, Constants.password);

            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.MONTH, 2);

                KeyPair keyPair = CertificateGenerator.generateKeyPair();
                SubjectData subjectData = CertificateGenerator.generateSubjectData("Security Admin", "Tim20", "Tim20Root", "Novi Sad", "tim20@gmail.com", new Date(), calendar.getTime(), keyPair);
                IssuerData issuerData = CertificateGenerator.generateIssuerData("Security Admin", "Tim20", "Tim20Root", "Novi Sad", "tim20@gmail.com", keyPair.getPrivate(), IETFUtils.valueToString(subjectData.getX500name().getRDNs(BCStyle.UID)[0].getFirst().getValue()));

                CertificateGenerator cg = new CertificateGenerator();
                X509Certificate cert = cg.generateCertificate(subjectData, issuerData, BigInteger.ZERO, new KeyUsages[]{KeyUsages.KEY_CERT_SIGN, KeyUsages.CRL_SIGN });

                keyStoreService.write(keyStore, "Security Admin", keyPair.getPrivate(), Constants.password.toCharArray(), cert, null);
                keyStoreService.saveKeyStore(keyStore, Constants.keystoreFilePath, Constants.password.toCharArray());

                Certificate certificate = keyStoreService.readCertificate(keyStore, "Security Admin");
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

        } else {
            System.out.println("Postoji");

            try {
                KeyStore keyStore = keyStoreService.getKeyStore(Constants.keystoreFilePath, Constants.password);

                ArrayList<Certificate> list = keyStoreService.findAllCertificates(keyStore);
                CertificateGenerator.serialNumber = list.size();
                for(Certificate certificate: list){
                    X509Certificate c = (X509Certificate) certificate;

                    System.out.println("Issuer\n");
                    System.out.println(c.getIssuerDN().getName());
                    System.out.println("Subject\n");
                    System.out.println(c.getSubjectX500Principal().getName());

                    System.out.println(c.getNotAfter());
                    System.out.println(c.getNotBefore());
                }

            } catch (Exception e) {
                System.out.println("Eror se dogodio");
                e.printStackTrace();
            }

        }
    }
}
