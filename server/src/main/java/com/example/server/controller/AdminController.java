package com.example.server.controller;

import com.example.server.dto.CertificateDTO;
import com.example.server.dto.CertificateExchangeDTO;
import com.example.server.enumeration.KeyUsages;
import com.example.server.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PreAuthorize("hasAuthority('PKI_ADMINISTRATION')")
    @RequestMapping(value="/createCertificate", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CertificateDTO> issueCertificate(@RequestBody CertificateDTO certificateDTO) {
        try{
            System.out.println(certificateDTO.getCommonName());
            if(certificateDTO.getKeyUsages().length < 1) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
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
