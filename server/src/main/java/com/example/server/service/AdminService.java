package com.example.server.service;

import com.example.server.certificates.CertificateGenerator;
import com.example.server.certificates.Constants;
import com.example.server.data.IssuerData;
import com.example.server.data.SubjectData;
import com.example.server.dto.CertificateDTO;
import com.example.server.dto.CertificateExchangeDTO;
import com.example.server.keystore.KeyStoreReader;
import com.example.server.keystore.KeyStoreWriter;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

@Service
public class AdminService {

    private KeyStoreReader keyStoreReader = new KeyStoreReader();
    private KeyStoreWriter keyStoreWriter = new KeyStoreWriter();

    public CertificateDTO createCertificate(CertificateDTO certificateDTO) {
        try {
            keyStoreWriter.loadKeyStore(Constants.keystoreFilePath, Constants.password.toCharArray());

            SubjectData subjectData = CertificateGenerator.generateSubjectData(certificateDTO.getCommonName(),certificateDTO.getOrganization(), certificateDTO.getOrganizationalUnit(), certificateDTO.getCity(), certificateDTO.getMail());
            KeyPair keyPair = CertificateGenerator.generateKeyPair();

            X509Certificate issuerCert = (X509Certificate) keyStoreReader.readCertificateBySerialNumber(Constants.keystoreFilePath, Constants.password, certificateDTO.getIssuer());

            X500Name x500name = new JcaX509CertificateHolder(issuerCert).getSubject();
            RDN cn = x500name.getRDNs(BCStyle.CN)[0];
            RDN org = x500name.getRDNs(BCStyle.O)[0];
            RDN email = x500name.getRDNs(BCStyle.E)[0];
            RDN ou = x500name.getRDNs(BCStyle.OU)[0];
            RDN city = x500name.getRDNs(BCStyle.C)[0];

            IssuerData issuerData = CertificateGenerator.generateIssuerData(rdnToString(cn), rdnToString(org),rdnToString(ou),rdnToString(city),rdnToString(email), keyStoreReader.getPrivateKey(Constants.keystoreFilePath, rdnToString(cn), Constants.password));

            CertificateGenerator cg = new CertificateGenerator();
            X509Certificate cert = cg.generateCertificate(subjectData, issuerData, certificateDTO.getKeyUsages());

            keyStoreWriter.write(certificateDTO.getCommonName(), keyPair.getPrivate(), Constants.password.toCharArray(), (Certificate) cert);
            keyStoreWriter.saveKeyStore(Constants.keystoreFilePath, Constants.password.toCharArray());

            Certificate certificate = keyStoreReader.readCertificate(Constants.keystoreFilePath, Constants.password, certificateDTO.getCommonName());
            X509Certificate c = (X509Certificate) certificate;

            System.out.println("Issuer\n");
            System.out.println(c.getIssuerDN().getName());

            System.out.println("Subject\n");
            System.out.println(c.getSubjectX500Principal().getName());
            System.out.println(c.getNotAfter());
            System.out.println(c.getNotBefore());
        } catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String rdnToString(RDN rdn) {
        return IETFUtils.valueToString(rdn.getFirst().getValue());
    }

    public ArrayList<CertificateExchangeDTO> getCACerts() {
        try {
            return keyStoreReader.findCACerts(keyStoreReader.getKeyStore(Constants.keystoreFilePath, Constants.password));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}