package com.example.server.controller;

import com.example.server.dto.CertificateDTO;
import com.example.server.dto.CertificateExchangeDTO;
import com.example.server.enumeration.KeyUsages;
import com.example.server.exception.CustomException;
import com.example.server.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.regex.Pattern;

@RestController
public class AdminController {

    private static final Pattern emailPattern = Pattern.compile("^[a-zA-Z0-9._-]+@(.+)$");
    private static final Pattern commonNamePattern = Pattern.compile("^[a-zA-Z0-9 '-]+$");

    @Autowired
    private AdminService adminService;

    @PreAuthorize("hasAuthority('PKI_ADMINISTRATION')")
    @RequestMapping(value="/createCertificate", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CertificateDTO> issueCertificate(@RequestBody CertificateDTO certificateDTO) {
        try{
            if(certificateDTO.getCommonName().equals("") || certificateDTO.getCity().equals("") || certificateDTO.getCountry().equals("") || certificateDTO.getCountyOfState().equals("") || certificateDTO.getMail().equals("") || certificateDTO.getNotBefore()==null || certificateDTO.getNotAfter()==null || certificateDTO.getKeyUsages().length==0 || certificateDTO.getIssuer().equals("") || certificateDTO.getOrganization().equals("")){
                throw new CustomException("Fields must not be empty.", HttpStatus.NOT_ACCEPTABLE);
            }else if(!emailPattern.matcher(certificateDTO.getMail()).matches()){
                throw new CustomException("Improper email format.", HttpStatus.NOT_ACCEPTABLE);
            }else if(!commonNamePattern.matcher(certificateDTO.getCommonName()).matches()){
                throw new CustomException("Common name doesn't match requirements.", HttpStatus.NOT_ACCEPTABLE);
            }
            return new ResponseEntity<>(adminService.createCertificate(certificateDTO), HttpStatus.CREATED);
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('PKI_ADMINISTRATION')")
    @RequestMapping(value="/getAllKeyUsages", method = RequestMethod.GET)
    public ResponseEntity<KeyUsages[]> getAllKeyUsages() {
        try {
            return new ResponseEntity<>(KeyUsages.values(), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('PKI_ADMINISTRATION')")
    @RequestMapping(value="/getAllIssuerCerts", method = RequestMethod.PUT)
    public ResponseEntity<ArrayList<CertificateExchangeDTO>> getAllIssuerCerts(@RequestBody KeyUsages[] keyUsages) {
        try {
            if(keyUsages.length <1){
                throw new CustomException("Should contain at least one key usage.", HttpStatus.NOT_ACCEPTABLE);
            }
            return new ResponseEntity<>(adminService.getCACerts( keyUsages), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('PKI_ADMINISTRATION')")
    @RequestMapping(value = "/getAllCerts", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<CertificateExchangeDTO>> getAllCerts(){
        try{
            return new ResponseEntity<>(adminService.getAllCerts(), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('PKI_ADMINISTRATION')")
    @RequestMapping(value = "/getAllRevocatedCerts", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<CertificateExchangeDTO>> getAllRevocatedCerts(){
        try{
            return new ResponseEntity<>(adminService.getAllRevocatedCerts(), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('PKI_ADMINISTRATION')")
    @RequestMapping(value="/download", method = RequestMethod.PUT)
    public ResponseEntity<CertificateExchangeDTO> downloadCertificate(@RequestBody CertificateExchangeDTO certificateExchangeDTO){
        try{
            if(certificateExchangeDTO.getName().equals("") || certificateExchangeDTO.getEmail().equals("") || certificateExchangeDTO.getIssuerName().equals("") || certificateExchangeDTO.getEmail().equals("") || certificateExchangeDTO.getOrganization().equals("") || certificateExchangeDTO.getNotBefore()==null || certificateExchangeDTO.getNotAfter()==null || certificateExchangeDTO.getReason().equals("") || certificateExchangeDTO.getSerialNumber().equals("")){
                throw new CustomException("Fields must not be empty.", HttpStatus.NOT_ACCEPTABLE);
            }else if(!emailPattern.matcher(certificateExchangeDTO.getEmail()).matches()) {
                throw new CustomException("Improper email format.", HttpStatus.NOT_ACCEPTABLE);
            }else if(!commonNamePattern.matcher(certificateExchangeDTO.getName()).matches()){
                throw new CustomException("Common name doesn't match requirements.", HttpStatus.NOT_ACCEPTABLE);
            }
            CertificateExchangeDTO c = adminService.downloadCertificate(certificateExchangeDTO);
            if(c!= null){
                return new ResponseEntity<>(c, HttpStatus.OK);
            }else {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }


    @PreAuthorize("hasAuthority('PKI_ADMINISTRATION')")
    @RequestMapping(value="/revokeCertificate/{reason}", method = RequestMethod.PUT)
    public ResponseEntity<CertificateExchangeDTO> revokeCertificate(@PathVariable("reason") String reason, @RequestBody CertificateExchangeDTO certificateExchangeDTO){

        try{
            if(certificateExchangeDTO.getName().equals("") || certificateExchangeDTO.getEmail().equals("") || certificateExchangeDTO.getIssuerName().equals("") || certificateExchangeDTO.getEmail().equals("") || certificateExchangeDTO.getOrganization().equals("") || certificateExchangeDTO.getNotBefore()==null || certificateExchangeDTO.getNotAfter()==null || reason.equals("") || certificateExchangeDTO.getSerialNumber().equals("")){
                throw new CustomException("Fields must not be empty.", HttpStatus.NOT_ACCEPTABLE);
            }else if(!emailPattern.matcher(certificateExchangeDTO.getEmail()).matches()){
                throw new CustomException("Improper email format.", HttpStatus.NOT_ACCEPTABLE);
            }
            CertificateExchangeDTO c = adminService.revokeCertificate(certificateExchangeDTO, reason);

            if(c != null){
                return new ResponseEntity<>(c, HttpStatus.OK);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasAuthority('PKI_ADMINISTRATION')")
    @RequestMapping(value = "/check", method = RequestMethod.PUT)
    public ResponseEntity<CertificateExchangeDTO> checkValidity(@RequestBody CertificateExchangeDTO certificateExchangeDTO){
        try{
            if(certificateExchangeDTO.getName().equals("") || certificateExchangeDTO.getEmail().equals("") || certificateExchangeDTO.getIssuerName().equals("") || certificateExchangeDTO.getEmail().equals("") || certificateExchangeDTO.getOrganization().equals("") || certificateExchangeDTO.getNotBefore()==null || certificateExchangeDTO.getNotAfter()==null || certificateExchangeDTO.getReason().equals("") || certificateExchangeDTO.getSerialNumber().equals("")){
                throw new CustomException("Fields must not be empty.", HttpStatus.NOT_ACCEPTABLE);
            }else if(!emailPattern.matcher(certificateExchangeDTO.getEmail()).matches()){
                throw new CustomException("Improper email format.", HttpStatus.NOT_ACCEPTABLE);
            }else if(!commonNamePattern.matcher(certificateExchangeDTO.getName()).matches()){
                throw new CustomException("Common name doesn't match requirements.", HttpStatus.NOT_ACCEPTABLE);
            }
            CertificateExchangeDTO c = adminService.checkValidity(certificateExchangeDTO);
            if(c!= null){
                return new ResponseEntity<CertificateExchangeDTO>(c, HttpStatus.OK);
            }else{
                return new ResponseEntity<>(null, HttpStatus.CONFLICT);
            }

        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
