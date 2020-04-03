package com.example.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

@Service
public class KeyStoreService {
    private KeyStore keyStore;

    @Autowired
    public KeyStoreService() {
        try {
            this.keyStore = KeyStore.getInstance("JKS", "SUN");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void loadKeyStore(String fileName, char[] password) {
        try {
            if(fileName != null) {
                keyStore.load(new FileInputStream(fileName), password);
            } else {
                //Ako je cilj kreirati novi KeyStore poziva se i dalje load, pri cemu je prvi parametar null
                keyStore.load(null, password);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveKeyStore(String fileName, char[] password) {
        try {
            keyStore.store(new FileOutputStream(fileName), password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Upisujemo privatni kljuc
    public void writeKeyEntry(String alias, PrivateKey privateKey, char[] password, java.security.cert.Certificate certificate) {
        try {
            keyStore.setKeyEntry(alias, privateKey, password, new Certificate[] {certificate});
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }

    //Upisujemo sertifikat
    public void writeCertificateEntry(String alias, Certificate certificate) {
        try {
            keyStore.setCertificateEntry(alias, certificate);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //ucitavanje sertifikata
    public Certificate readCertificate(String keyStoreFile, String keyStorePass, String alias) {
        try {
            //kreiramo instancu KeyStore
            KeyStore ks = KeyStore.getInstance("JKS", "SUN");
            //ucitavamo podatke
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
            ks.load(in, keyStorePass.toCharArray());

            if(ks.isKeyEntry(alias)) {
                Certificate cert = ks.getCertificate(alias);
                return cert;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //ucitavanje privatnog kljuca iz ks fajla
    public PrivateKey readPrivateKey(String keyStoreFile, String keyStorePass, String alias, String pass) {
        try {
            //kreiramo instancu KeyStore
            KeyStore ks = KeyStore.getInstance("JKS", "SUN");
            //ucitavamo podatke
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
            ks.load(in, keyStorePass.toCharArray());

            if(ks.isKeyEntry(alias)) {
                PrivateKey pk = (PrivateKey) ks.getKey(alias, pass.toCharArray());
                return pk;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
