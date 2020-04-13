package com.example.server.Repository;


import com.example.server.Model.CertificateModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface CertificateRepository extends JpaRepository<CertificateModel, Long> {

    List<CertificateModel> findAll();

    CertificateModel getBySerialNumber(String serialNumber);
}
