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
import java.security.spec.ECGenParameterSpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class CertificateGenerator {
    public static long serialNumber = 0;

    public CertificateGenerator() {}

    public X509Certificate generateCertificate(SubjectData subjectData, IssuerData issuerData, BigInteger issuerCSN, KeyUsages[] keyUsages) {
        try {
            JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithECDSA");

            builder = builder.setProvider("BC");

            ContentSigner contentSigner = builder.build(issuerData.getPrivateKey());

            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerData.getX500name(),
                    new BigInteger(subjectData.getSerialNumber()),
                    subjectData.getStartDate(),
                    subjectData.getEndDate(),
                    subjectData.getX500name(),
                    subjectData.getPublicKey());
            try {
                certGen.addExtension(Extension.keyUsage, false, getKeyUsage(keyUsages));
                AccessDescription caIssuers = new AccessDescription(AccessDescription.id_ad_caIssuers, new GeneralName(GeneralName.uniformResourceIdentifier, new DERIA5String("http://localhost:8080/files/certificates/") + issuerCSN.toString() + ".crt"));

                ASN1EncodableVector aia_ASN = new ASN1EncodableVector();
                aia_ASN.add(caIssuers);

                certGen.addExtension(Extension.authorityInfoAccess, false, new DERSequence(aia_ASN));
            }catch(Exception e) {
                e.printStackTrace();
            }

            X509CertificateHolder certHolder = certGen.build(contentSigner);

            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
            certConverter = certConverter.setProvider("BC");

            return certConverter.getCertificate(certHolder);
        } catch (Exception e) {
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

    public static IssuerData generateIssuerData(String CN, String O, String OU, String C, String E, PrivateKey issuerKey, String uid) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, CN);
        builder.addRDN(BCStyle.O, O);
        builder.addRDN(BCStyle.OU, OU);
        builder.addRDN(BCStyle.C, C);
        builder.addRDN(BCStyle.E, E);
        //UID (USER ID) je ID korisnika
        builder.addRDN(BCStyle.UID, uid);

        //Kreiraju se podaci za issuer-a, sto u ovom slucaju ukljucuje:
        // - privatni kljuc koji ce se koristiti da potpise sertifikat koji se izdaje
        // - podatke o vlasniku sertifikata koji izdaje nov sertifikat
        return new IssuerData(issuerKey, builder.build());
    }

    public static SubjectData generateSubjectData(String CN, String O, String OU, String C, String E, Date notBefore, Date notAfter, KeyPair keyPairParam) {
        try {
            KeyPair keyPairSubject;
            if(keyPairParam == null) {
                keyPairSubject = generateKeyPair();
            } else {
                keyPairSubject = keyPairParam;
            }

            SimpleDateFormat iso8601Formater = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = iso8601Formater.parse(iso8601Formater.format(notBefore));
            Date endDate = iso8601Formater.parse(iso8601Formater.format(notAfter));

            X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
            builder.addRDN(BCStyle.CN, CN);
            builder.addRDN(BCStyle.O, O);
            builder.addRDN(BCStyle.OU, OU);
            builder.addRDN(BCStyle.C, C);
            builder.addRDN(BCStyle.E, E);

            builder.addRDN(BCStyle.UID, String.valueOf(UUID.randomUUID()));


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
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC", "SunEC");
            ECGenParameterSpec ecsp = new ECGenParameterSpec("secp256r1");
            keyGen.initialize(ecsp);
            return keyGen.genKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
