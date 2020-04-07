package com.example.server.Repository;


import com.example.server.Model.CertificateModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends JpaRepository<CertificateModel, Long> {

    List<CertificateModel> findAll();

    Optional<CertificateModel> findBySerialNumber(String serialNumber);
}
