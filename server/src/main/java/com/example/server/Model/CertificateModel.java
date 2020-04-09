package com.example.server.Model;

import javax.persistence.*;

@Entity
public class CertificateModel {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @Column(name="serial_number", nullable = false, unique = true)
    private String serialNumber;

    @Column(name="revoke_reason")
    private String revokeReason;

    public CertificateModel() {

    }

    public CertificateModel(String serialNumber) {
        this.serialNumber = serialNumber;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getRevokeReason() {
        return revokeReason;
    }

    public void setRevokeReason(String revokeReason) {
        this.revokeReason = revokeReason;
    }
}
