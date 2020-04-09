package com.example.server.service;

import com.example.server.Model.CertificateModel;
import com.example.server.Repository.CertificateRepository;
import com.example.server.certificates.Constants;
import com.example.server.dto.CertificateExchangeDTO;
import com.example.server.keystore.KeyStoreReader;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.jcajce.provider.asymmetric.X509;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.KeyFactorySpi;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.*;

@Service
public class CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;


    public CertificateModel revokeCertificate(X509Certificate certificate, String reason) {
            CertificateModel certificateModel = new CertificateModel();
            certificateModel.setSerialNumber(certificate.getSerialNumber().toString());
            certificateModel.setRevokeReason(reason);

            certificateRepository.save(certificateModel);

            return certificateModel;
    }

    public ArrayList<CertificateExchangeDTO> certificateCheckDate(ArrayList<CertificateExchangeDTO> certList) {

        ArrayList<CertificateExchangeDTO> retList = new ArrayList<>();

        Date today = Calendar.getInstance().getTime();

        for(CertificateExchangeDTO certificate : certList){
            if(certificate.getNotAfter().before(today)){
                CertificateModel certificateModel = certificateRepository.getBySerialNumber(certificate.getSerialNumber().toString());

                certificateModel.setRevokeReason("EXPIRED");

                certificateRepository.save(certificateModel);
                continue;
            }

            retList.add(certificate);

        }
        return retList;
    }

}
