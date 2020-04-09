package com.example.server.keystore;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

public class KeyStoreWriter {
    private KeyStore keyStore;

    public KeyStoreWriter() {
        try {
            keyStore = KeyStore.getInstance("PKCS12", new BouncyCastleProvider());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public KeyStore getKeystore(){
        return keyStore;
    }

    public void loadKeyStore(String fileName, char[] password) {
        try {
            if(fileName != null) {
                keyStore.load(new FileInputStream(fileName), password);
            } else {
                //Ako je cilj kreirati novi KeyStore poziva se i dalje load, pri cemu je prvi parametar null
                keyStore.load(null, password);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveKeyStore(String fileName, char[] password) {
        try {
            keyStore.store(new FileOutputStream(fileName), password);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeInitialRoot(String alias, PrivateKey privateKey, char[] password, Certificate certificate) {
        try {
            keyStore.setKeyEntry(alias, privateKey, password, new Certificate[] {
                    certificate
            });

        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }

    public void write(String alias, PrivateKey privateKey, char[] password, Certificate certificate, X509Certificate issuer) {
        try {

            keyStore.setKeyEntry(alias, privateKey, password, getCertificateChain(issuer.getSerialNumber().toString(), certificate));

        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }


    public Certificate[] getCertificateChain(String serialNumber, Certificate certificate){
        try {
            Enumeration<String> aliases = keyStore.aliases();
            while(aliases.hasMoreElements()) {
                String entry = aliases.nextElement();
                X509Certificate cert = (X509Certificate) keyStore.getCertificate(entry);
                if(cert.getSerialNumber().toString().equals(serialNumber)) {
//                    List<Certificate> list = Arrays.asList(keyStore.getCertificateChain(entry));
//                    List<Certificate> retList = new ArrayList<>();
//                    retList.add( certificate);
//                    for(Certificate c : list) {
//                        retList.add(c);
//                    }
//                    return (Certificate[]) retList.toArray();
                    List<Certificate> certificateList = new ArrayList<>(Arrays.asList((keyStore.getCertificateChain(entry))));
                    certificateList.add(0, certificate);

                    Certificate[] newCertificates = new Certificate[certificateList.size()];
                    for (int i = 0; i < certificateList.size(); i++) {
                        System.out.println(((X509Certificate)certificateList.get(i)).getSerialNumber());
                        newCertificates[i] = certificateList.get(i);
                    }

                    System.out.println("Duzina chaina " + newCertificates.length);
                    return newCertificates;
                }
            }
            return null;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
