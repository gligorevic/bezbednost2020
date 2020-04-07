package com.example.server.service;

import com.example.server.Model.CertificateModel;
import com.example.server.Repository.CertificateRepository;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.List;

@Service
public class CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

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
}
