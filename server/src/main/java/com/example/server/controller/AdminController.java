package com.example.server.controller;

import com.example.server.dto.CertificateDTO;
import com.example.server.dto.CertificateExchangeDTO;
import com.example.server.enumeration.KeyUsages;
import com.example.server.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping(value="/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

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

    @RequestMapping(value="/getAllKeyUsages", method = RequestMethod.GET)
    public ResponseEntity<KeyUsages[]> getAllKeyUsages() {
        try {
            return new ResponseEntity<>(KeyUsages.values(), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value="/getAllIssuerCerts", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<CertificateExchangeDTO>> getAllIssuerCerts() {
        try {
            return new ResponseEntity<>(adminService.getCACerts(), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/getAllCerts", method = RequestMethod.GET)
    public ResponseEntity<ArrayList<CertificateExchangeDTO>> getAllCerts(){
        try{
            return new ResponseEntity<>(adminService.getAllCerts(), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
