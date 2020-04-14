package com.example.server.dto;

import java.math.BigInteger;
import java.util.Date;

public class CertificateExchangeDTO {
    private String name;
    private BigInteger serialNumber;
    private String organization;
    private String email;
    private String issuerName;
    private Date notBefore;
    private Date notAfter;
    private String reason;

    public CertificateExchangeDTO(String name, String organization, String email, String issuerName, BigInteger serialNumber, Date notBefore, Date notAfter) {
        this.name = name;
        this.serialNumber = serialNumber;
        this.organization = organization;
        this.email = email;
        this.issuerName = issuerName;
        this.notBefore = notBefore;
        this.notAfter = notAfter;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigInteger getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(BigInteger serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public Date getNotBefore() {
        return notBefore;
    }

    public void setNotBefore(Date notBefore) {
        this.notBefore = notBefore;
    }

    public Date getNotAfter() {
        return notAfter;
    }

    public void setNotAfter(Date notAfter) {
        this.notAfter = notAfter;
    }
}
