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

    KeyStoreReader keyStoreReader = new KeyStoreReader();

    public void addFromKeyStoreToRepository(List<Certificate> certificates){
        try{

            for(Certificate certificate : certificates){
                X509Certificate x509Cert = (X509Certificate) certificate;

                String serialNum = ((X509Certificate) certificate).getSerialNumber().toString();


            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void addCertificate(X509Certificate certificate) throws CertificateEncodingException {

        String serialNum = certificate.getSerialNumber().toString();

        X500Name x500nameSubject = new JcaX509CertificateHolder(certificate).getSubject();
        RDN cnSubject = x500nameSubject.getRDNs(BCStyle.CN)[0];
        String subjectAlias = IETFUtils.valueToString(cnSubject.getFirst().getValue());

        X500Name x500nameIssuer = new JcaX509CertificateHolder(certificate).getIssuer();
        RDN cnIssuer = x500nameIssuer.getRDNs(BCStyle.CN)[0];
        String issuerAlias = IETFUtils.valueToString(cnIssuer.getFirst().getValue());

        CertificateModel certificateModel = new CertificateModel(serialNum, true, "", subjectAlias, issuerAlias);

        certificateRepository.save(certificateModel);
    }

    public void addCertificate(String serialNumber, String alias, String issuerAlias){

        CertificateModel certificateModel = new CertificateModel(serialNumber, true, "", alias, issuerAlias);

        certificateRepository.save(certificateModel);

    }

    public CertificateModel revokeCertificate(X509Certificate certificate, String reason) {

        String serialNum = certificate.getSerialNumber().toString();

        CertificateModel certificateModel = certificateRepository.findBySerialNumber(serialNum).get();

        if(certificateModel != null){
            certificateModel.setActive(false);
            certificateModel.setRevokeReason(reason);
            certificateRepository.save(certificateModel);

            return certificateModel;
        }

        return null;
    }


    public boolean certificateChainIsOk(CertificateModel certificateModel) throws KeyStoreException, CertificateEncodingException {

        System.out.println("POKRENUO SE");
        if(certificateModel != null && certificateModel.isActive()){

            certificateModel = certificateRepository.findByAlias(certificateModel.getIssuerAlias()).get();

            // proveravaj validnost kroz lanac, prema gore
            do{

                if(certificateModel.isActive()){
                    certificateModel = certificateRepository.findByAlias(certificateModel.getIssuerAlias()).get();
                    continue;
                }else{
                    return false;
                }
            }while(!certificateModel.getAlias().equals(certificateModel.getAlias()));

            if(!certificateModel.isActive()){
                return false;
            }

            return true;
        }

        return false;
    }

    public ArrayList<CertificateExchangeDTO> certificateCheckDate(ArrayList<CertificateExchangeDTO> certList) {

        ArrayList<CertificateExchangeDTO> retList = new ArrayList<>();

        Date today = Calendar.getInstance().getTime();

        for(CertificateExchangeDTO certificate : certList){
            if(certificate.getNotAfter().before(today)){
                CertificateModel certificateModel = certificateRepository.findBySerialNumber(certificate.getSerialNumber().toString()).get();

                certificateModel.setActive(false);
                certificateModel.setRevokeReason("EXPIRED");

                certificateRepository.save(certificateModel);
                continue;
            }

            retList.add(certificate);

        }
        return retList;
    }
}
