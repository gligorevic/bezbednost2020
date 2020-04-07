package com.example.server.Model;

import javax.persistence.*;

@Entity
public class CertificateModel {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(name="serial_number", nullable = false, unique = true)
    private String serialNumber;

    @Column(name="alias", nullable = false)
    private String alias;

    @Column(name="active", nullable = false)
    private boolean isActive = true;

    @Column(name="revoke_reason")
    private String revokeReason;

    @Column(name="issuer_alias", nullable = false)
    private String issuerAlias;

    public CertificateModel() {

    }

    public CertificateModel(String serialNumber, boolean isActive, String revokeReason, String alias, String issuerAlias) {
        this.serialNumber = serialNumber;
        this.isActive = isActive;
        this.revokeReason = revokeReason;
        this.alias = alias;
        this.issuerAlias = issuerAlias;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getRevokeReason() {
        return revokeReason;
    }

    public void setRevokeReason(String revokeReason) {
        this.revokeReason = revokeReason;
    }

    public String getIssuerAlias() {
        return issuerAlias;
    }

    public void setIssuerAlias(String issuerAlias) {
        this.issuerAlias = issuerAlias;
    }
}
