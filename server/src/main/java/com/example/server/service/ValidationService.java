package com.example.server.service;

import org.springframework.stereotype.Service;

import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;

@Service
public class ValidationService {


    public boolean validateChain(Certificate[] certChain) throws CertificateNotYetValidException, CertificateExpiredException {

        boolean valid = true;

        for (int i = 0; i < certChain.length - 1; i++) {
            Certificate cer = certChain[i];
            System.out.println("USAO" + i);
            if (!verifySignature(cer, certChain[i + 1])) {
                valid = false;
            }
        }

        if(valid){
            System.out.println("SVE OKE");
        }

        return valid;
    }

    private boolean verifySignature(Certificate certificate, Certificate issuerCertificate){

        try {
            certificate.verify(issuerCertificate.getPublicKey());
            return true;
        } catch (SignatureException | InvalidKeyException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return false;
    }
}
