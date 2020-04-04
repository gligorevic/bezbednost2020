package com.example.server.certificates;

import com.example.server.data.IssuerData;
import com.example.server.data.SubjectData;
import com.example.server.enumeration.KeyUsages;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class CertificateGenerator {
    private static long serialNumber = 0;

    public CertificateGenerator() {}

    public X509Certificate generateCertificate(SubjectData subjectData, IssuerData issuerData, KeyUsages[] keyUsages) {
        try {
            //Posto klasa za generisanje sertifiakta ne moze da primi direktno privatni kljuc pravi se builder za objekat
            //Ovaj objekat sadrzi privatni kljuc izdavaoca sertifikata i koristiti se za potpisivanje sertifikata
            //Parametar koji se prosledjuje je algoritam koji se koristi za potpisivanje sertifiakta
            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
            //Takodje se navodi koji provider se koristi, u ovom slucaju Bouncy Castle
            builder = builder.setProvider(new BouncyCastleProvider());

            //Formira se objekat koji ce sadrzati privatni kljuc i koji ce se koristiti za potpisivanje sertifikata
            ContentSigner contentSigner = builder.build(issuerData.getPrivateKey());

            //Postavljaju se podaci za generisanje sertifiakta
            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerData.getX500name(),
                    new BigInteger(subjectData.getSerialNumber()),
                    subjectData.getStartDate(),
                    subjectData.getEndDate(),
                    subjectData.getX500name(),
                    subjectData.getPublicKey());
            //Generise se sertifikat
            try {
                for(KeyUsages key : keyUsages) {
                    System.out.println(key);
                    System.out.println(getKeyUsage(key));
                    certGen.addExtension(Extension.keyUsage, false, getKeyUsage(key));
                }
            }catch(Exception e) {
                e.printStackTrace();
            }

            X509CertificateHolder certHolder = certGen.build(contentSigner);

            //Builder generise sertifikat kao objekat klase X509CertificateHolder
            //Nakon toga je potrebno certHolder konvertovati u sertifikat, za sta se koristi certConverter
            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
            certConverter = certConverter.setProvider(new BouncyCastleProvider());

            //Konvertuje objekat u sertifikat
            return certConverter.getCertificate(certHolder);
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (OperatorCreationException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }

    private KeyUsage getKeyUsage(KeyUsages key) throws Exception {
        switch(key) {
            case CRL_SIGN:
                return new KeyUsage(KeyUsage.cRLSign);
            case DATA_ENCIPHERMENT:
                return new KeyUsage(KeyUsage.dataEncipherment);
            case DECIPHER_ONLY:
                return new KeyUsage(KeyUsage.decipherOnly);
            case DIGITAL_SIGNATURE:
                return new KeyUsage(KeyUsage.digitalSignature);
            case ENCIPHER_ONLY:
                return new KeyUsage(KeyUsage.encipherOnly);
            case KEY_AGREEMENT:
                return new KeyUsage(KeyUsage.keyAgreement);
            case KEY_CERT_SIGN:
                return new KeyUsage(KeyUsage.keyCertSign);
            case KEY_ENCIPHERMENT:
                return new KeyUsage(KeyUsage.keyEncipherment);
            case NON_REPUDIATION:
                return new KeyUsage(KeyUsage.nonRepudiation);
            default:
                throw new Exception("Bad key ussage");
        }
    }

    public static IssuerData generateIssuerData(String CN, String O, String OU, String C, String E, PrivateKey issuerKey) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, CN);
        builder.addRDN(BCStyle.O, O);
        builder.addRDN(BCStyle.OU, OU);
        builder.addRDN(BCStyle.C, C);
        builder.addRDN(BCStyle.E, E);
        //UID (USER ID) je ID korisnika
        builder.addRDN(BCStyle.UID, String.valueOf(UUID.randomUUID()));

        //Kreiraju se podaci za issuer-a, sto u ovom slucaju ukljucuje:
        // - privatni kljuc koji ce se koristiti da potpise sertifikat koji se izdaje
        // - podatke o vlasniku sertifikata koji izdaje nov sertifikat
        return new IssuerData(issuerKey, builder.build());
    }

    public static SubjectData generateSubjectData(String CN, String O, String OU, String C, String E) {
        try {
            KeyPair keyPairSubject = generateKeyPair();

            //Datumi od kad do kad vazi sertifikat
            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.YEAR, 20);
            SimpleDateFormat iso8601Formater = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = iso8601Formater.parse(iso8601Formater.format(new Date()));
            Date endDate = iso8601Formater.parse(iso8601Formater.format(c.getTime()));

            //klasa X500NameBuilder pravi X500Name objekat koji predstavlja podatke o vlasniku
            X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
            builder.addRDN(BCStyle.CN, CN);
            builder.addRDN(BCStyle.O, O);
            builder.addRDN(BCStyle.OU, OU);
            builder.addRDN(BCStyle.C, C);
            builder.addRDN(BCStyle.E, E);
            //UID (USER ID) je ID korisnika
            builder.addRDN(BCStyle.UID, String.valueOf(UUID.randomUUID()));

            //Kreiraju se podaci za sertifikat, sto ukljucuje:
            // - javni kljuc koji se vezuje za sertifikat
            // - podatke o vlasniku
            // - serijski broj sertifikata
            // - od kada do kada vazi sertifikat
            return new SubjectData(keyPairSubject.getPublic(), builder.build(), addSerialNumber(), startDate, endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String addSerialNumber() {
        long ser = serialNumber;
        serialNumber++;
        return String.valueOf(ser);
    }

    public static KeyPair generateKeyPair() {
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
