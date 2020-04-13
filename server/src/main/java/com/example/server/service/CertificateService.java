package com.example.server.service;

import com.example.server.Model.CertificateModel;
import com.example.server.Repository.CertificateRepository;
import com.example.server.dto.CertificateExchangeDTO;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

                if(certificateModel == null) {
                    certificateModel = new CertificateModel(certificate.getSerialNumber().toString());
                }

                certificateModel.setRevokeReason("EXPIRED");


                certificateRepository.save(certificateModel);
                continue;
            }

            retList.add(certificate);

        }
        return retList;
    }

    public boolean checkPrivateKeyDuration(X509Certificate issuerCert) {
        try {
            byte[] encodedExtensionValue = issuerCert.getExtensionValue("2.5.29.16");
            if (encodedExtensionValue != null) {
                ASN1Primitive extensionValue = JcaX509ExtensionUtils.parseExtensionValue(encodedExtensionValue);
                Date notBefore = ASN1GeneralizedTime.getInstance((ASN1TaggedObject) ((ASN1Sequence) extensionValue).getObjectAt(0), false).getDate();
                Date notAfter = ASN1GeneralizedTime.getInstance((ASN1TaggedObject) ((ASN1Sequence) extensionValue).getObjectAt(1), false).getDate();

                Date today = new Date();
                System.out.println(today);

                if(today.before(notBefore)) {
                    return false;
                }
                if(today.after(notAfter)) {
                    return false;
                }

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

}
