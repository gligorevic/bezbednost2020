package com.example.server.configuration;

import com.example.server.certificates.CertificateGenerator;
import com.example.server.data.IssuerData;
import com.example.server.data.SubjectData;
import com.example.server.enumeration.KeyUsages;
import com.example.server.keystore.KeyStoreReader;
import com.example.server.keystore.KeyStoreWriter;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

@Component
public class AppRunner implements ApplicationRunner {

    private KeyStore keyStore;

    private final String passString = "password";
    private final String keystorePath = "./files/keystore.p12";

    private KeyStoreReader keyStoreReader = new KeyStoreReader();
    private KeyStoreWriter keyStoreWriter = new KeyStoreWriter();

    public AppRunner() {Security.addProvider(new BouncyCastleProvider());}

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            keyStore = KeyStore.getInstance("PKCS12");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        File f = new File("./files/keystore.p12");

        if(!f.exists()) {
            System.out.println("Ne postoji keystore");

            char[] password = passString.toCharArray();

            keyStoreWriter.loadKeyStore(null,password);

            try {
                SubjectData subjectData = CertificateGenerator.generateSubjectData("Security Admin", "Tim20", "Tim20Root", "Novi Sad", "tim20@gmail.com");
                KeyPair keyPair = CertificateGenerator.generateKeyPair();
                IssuerData issuerData = CertificateGenerator.generateIssuerData("Security Admin", "Tim20", "Tim20Root", "Novi Sad", "tim20@gmail.com",keyPair.getPrivate());

                CertificateGenerator cg = new CertificateGenerator();
                X509Certificate cert = cg.generateCertificate(subjectData, issuerData, new KeyUsages[]{KeyUsages.KEY_CERT_SIGN, KeyUsages.CRL_SIGN });

                keyStoreWriter.write("Security Admin", keyPair.getPrivate(), password, (Certificate)cert);
                keyStoreWriter.saveKeyStore(keystorePath, password);

                Certificate certificate = keyStoreReader.readCertificate(keystorePath, passString, "Security Admin");
                X509Certificate c = (X509Certificate) certificate;

                System.out.println("Issuer\n");
                System.out.println(c.getIssuerDN().getName());
                System.out.println("\n\n");


                System.out.println("Subject\n");
                System.out.println(c.getSubjectX500Principal().getName());
                System.out.println("\n\n");

                System.out.println(c.getNotAfter());
                System.out.println(c.getNotBefore());
            } catch(Exception e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Postoji");

            Certificate certificate;
            try {
                certificate = keyStoreReader.readCertificate(keystorePath, passString, "tim20root");
                X509Certificate c = (X509Certificate) certificate;

                System.out.println("Issuer\n");
                System.out.println(c.getIssuerDN().getName());
                System.out.println("\n\n");


                System.out.println("Subject\n");
                System.out.println(c.getSubjectX500Principal().getName());
                System.out.println("\n\n");

                System.out.println(c.getNotAfter());
                System.out.println(c.getNotBefore());
            } catch (Exception e) {
                System.out.println("Eror se desio");
                e.printStackTrace();
            }

        }
    }




}
