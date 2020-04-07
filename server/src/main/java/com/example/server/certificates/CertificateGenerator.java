package com.example.server.certificates;

import com.example.server.data.IssuerData;
import com.example.server.data.SubjectData;
import com.example.server.enumeration.KeyUsages;
import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
    public static long serialNumber = 0;

    public CertificateGenerator() {}

    public X509Certificate generateCertificate(SubjectData subjectData, IssuerData issuerData, BigInteger issuerCSN, String aliasName, KeyUsages[] keyUsages) {
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

                    certGen.addExtension(Extension.keyUsage, false, getKeyUsage(keyUsages));

                    AccessDescription caIssuers = new AccessDescription(AccessDescription.id_ad_caIssuers,
                        new GeneralName(GeneralName.uniformResourceIdentifier,
                                new DERIA5String("http://localhost:8080/files/certificates/") + issuerCSN.toString() + ".crt"));


                ASN1EncodableVector aia_ASN = new ASN1EncodableVector();
                aia_ASN.add(caIssuers);

                certGen.addExtension(Extension.authorityInfoAccess, false, new DERSequence(aia_ASN));


            }catch(Exception e) {
                e.printStackTrace();
            }

            X509CertificateHolder certHolder = certGen.build(contentSigner);

            //Builder generise sertifikat kao objekat klase X509CertificateHolder
            //Nakon toga je potrebno certHolder konvertovati u sertifikat, za sta se koristi certConverter
            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
            certConverter = certConverter.setProvider(new BouncyCastleProvider());

            FileOutputStream os = new FileOutputStream(".\\files\\certificates\\" + serialNumber + ".cer");
            os.write("-----BEGIN CERTIFICATE-----\n".getBytes("US-ASCII"));
            os.write(Base64.encodeBase64(certConverter.getCertificate(certHolder).getEncoded(), true));
            os.write("-----END CERTIFICATE-----\n".getBytes("US-ASCII"));
            os.close();

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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private KeyUsage getKeyUsage(KeyUsages[] keyUsages) throws Exception {
        int keyValue = 0;
        for(KeyUsages key : keyUsages) {
            switch(key) {
                case CRL_SIGN:
                    keyValue = KeyUsage.cRLSign | keyValue;
                    break;
                case DATA_ENCIPHERMENT:
                    keyValue = KeyUsage.dataEncipherment | keyValue;
                    break;
                case DECIPHER_ONLY:
                    keyValue = KeyUsage.decipherOnly | keyValue;
                    break;
                case DIGITAL_SIGNATURE:
                    keyValue = KeyUsage.digitalSignature | keyValue;
                    break;
                case ENCIPHER_ONLY:
                    keyValue = KeyUsage.encipherOnly | keyValue;
                    break;
                case KEY_AGREEMENT:
                    keyValue = KeyUsage.keyAgreement | keyValue;
                    break;
                case KEY_CERT_SIGN:
                    keyValue = KeyUsage.keyCertSign | keyValue;
                    break;
                case KEY_ENCIPHERMENT:
                    keyValue = KeyUsage.keyEncipherment | keyValue;
                    break;
                case NON_REPUDIATION:
                    keyValue = KeyUsage.nonRepudiation | keyValue;
                    break;
                default:
                    throw new Exception("Bad key ussage");
            }
        }
        return new KeyUsage(keyValue);
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

    public static SubjectData generateSubjectData(String CN, String O, String OU, String C, String E, Date notBefore, Date notAfter) {
        try {
            KeyPair keyPairSubject = generateKeyPair();

            //Datumi od kad do kad vazi sertifikat
            SimpleDateFormat iso8601Formater = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = iso8601Formater.parse(iso8601Formater.format(notBefore));
            Date endDate = iso8601Formater.parse(iso8601Formater.format(notAfter));

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
