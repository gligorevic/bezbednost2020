package com.example.server.configuration;

import com.example.server.certificates.CertificateGenerator;
import com.example.server.data.IssuerData;
import com.example.server.data.SubjectData;
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
                SubjectData subjectData = generateSubjectData();
                KeyPair keyPair = generateKeyPair();
                IssuerData issuerData = generateIssuerData(keyPair.getPrivate());

                CertificateGenerator cg = new CertificateGenerator();
                X509Certificate cert = cg.generateCertificate(subjectData, issuerData);

                keyStoreWriter.write("tim20root", keyPair.getPrivate(), password, (Certificate)cert);
                keyStoreWriter.saveKeyStore(keystorePath, password);
//                keyStore.store(new FileOutputStream(keystorePath), password);

                Certificate certificate = keyStoreReader.readCertificate(keystorePath, passString, "tim20root");
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

    private IssuerData generateIssuerData(PrivateKey issuerKey) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, "Security Admin");
        builder.addRDN(BCStyle.O, "Tim20");
        builder.addRDN(BCStyle.OU, "Tim20Root");
        builder.addRDN(BCStyle.C, "RS");
        builder.addRDN(BCStyle.E, "tim20@gmail.com");
        //UID (USER ID) je ID korisnika
        builder.addRDN(BCStyle.UID, "654321");

        //Kreiraju se podaci za issuer-a, sto u ovom slucaju ukljucuje:
        // - privatni kljuc koji ce se koristiti da potpise sertifikat koji se izdaje
        // - podatke o vlasniku sertifikata koji izdaje nov sertifikat
        return new IssuerData(issuerKey, builder.build());
    }

    private SubjectData generateSubjectData() {
        try {
            KeyPair keyPairSubject = generateKeyPair();

            //Datumi od kad do kad vazi sertifikat
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.YEAR, 20);
            SimpleDateFormat iso8601Formater = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = iso8601Formater.parse(iso8601Formater.format(new Date()));
            Date endDate = iso8601Formater.parse(iso8601Formater.format(c.getTime()));

            //Serijski broj sertifikata
            String sn="1";

            //klasa X500NameBuilder pravi X500Name objekat koji predstavlja podatke o vlasniku
            X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
            builder.addRDN(BCStyle.CN, "Security Admin");
            builder.addRDN(BCStyle.O, "Tim20");
            builder.addRDN(BCStyle.OU, "Tim20Root");
            builder.addRDN(BCStyle.C, "RS");
            builder.addRDN(BCStyle.E, "tim20@gmail.com");
            //UID (USER ID) je ID korisnika
            builder.addRDN(BCStyle.UID, "654321");

            //Kreiraju se podaci za sertifikat, sto ukljucuje:
            // - javni kljuc koji se vezuje za sertifikat
            // - podatke o vlasniku
            // - serijski broj sertifikata
            // - od kada do kada vazi sertifikat
            return new SubjectData(keyPairSubject.getPublic(), builder.build(), sn, startDate, endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");

            // SecureRandom.getInstance - PARAMETRI -->
            // The name of the pseudo-random number generation (PRNG) algorithm supplied by the SUN provider.
            // This algorithm uses SHA-1 as the foundation of the PRNG. It computes the SHA-1 hash over a true-random
            // seed value concatenated with a 64-bit counter which is incremented by 1 for each operation.
            // From the 160-bit SHA-1 output, only 64 bits are used.

            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");

            // 2048 - The maximum key size that the provider supports for the cryptographic service.
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }
}
