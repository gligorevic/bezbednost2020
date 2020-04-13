package com.example.server.dto;

import com.example.server.enumeration.KeyUsages;

import java.util.Date;

public class CertificateDTO {
   private KeyUsages[] keyUsages;

   private String commonName;

   private String organization;

   private String organizationalUnit;

   private String city;

   private String countyOfState;

   private String country;

   private String mail;

   private String issuer;

   private String certificateType;

   private Date notBefore;

   private Date notAfter;

   public CertificateDTO() {

   }

   public CertificateDTO(String commonName, String organization, String organizationalUnit, String city, String countyOfState, String country, String mail, String issuer, Date notBefore, Date notAfter) {
      this.commonName = commonName;
      this.organization = organization;
      this.organizationalUnit = organizationalUnit;
      this.city = city;
      this.countyOfState = countyOfState;
      this.country = country;
      this.mail = mail;
      this.issuer = issuer;
      this.notBefore = notBefore;
      this.notAfter = notAfter;
   }

   public String getCertificateType() {
      return certificateType;
   }

   public void setCertificateType(String certificateType) {
      this.certificateType = certificateType;
   }

   public void setKeyUsages(KeyUsages[] keyUsages) {
      this.keyUsages = keyUsages;
   }

   public void setCommonName(String commonName) {
      this.commonName = commonName;
   }

   public void setOrganization(String organization) {
      this.organization = organization;
   }

   public void setOrganizationalUnit(String organizationalUnit) {
      this.organizationalUnit = organizationalUnit;
   }

   public void setCity(String city) {
      this.city = city;
   }

   public void setCountyOfState(String countyOfState) {
      this.countyOfState = countyOfState;
   }

   public void setCountry(String country) {
      this.country = country;
   }

   public void setMail(String mail) {
      this.mail = mail;
   }

   public void setIssuer(String issuer) {
      this.issuer = issuer;
   }

   public KeyUsages[] getKeyUsages() {
      return keyUsages;
   }

   public String getCommonName() {
      return commonName;
   }

   public String getOrganization() {
      return organization;
   }

   public String getOrganizationalUnit() {
      return organizationalUnit;
   }

   public String getCity() {
      return city;
   }

   public String getCountyOfState() {
      return countyOfState;
   }

   public String getCountry() {
      return country;
   }

   public String getMail() {
      return mail;
   }

   public String getIssuer() {
      return issuer;
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
