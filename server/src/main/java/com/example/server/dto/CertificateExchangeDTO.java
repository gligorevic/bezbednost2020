package com.example.server.dto;

import java.math.BigInteger;

public class CertificateExchangeDTO {
    private String name;
    private BigInteger serialNumber;
    private String organization;
    private String email;
    private String issuerName;

    public CertificateExchangeDTO(String name, String organization, String email, String issuerName, BigInteger serialNumber) {
        this.name = name;
        this.serialNumber = serialNumber;
        this.organization = organization;
        this.email = email;
        this.issuerName = issuerName;
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
}
