package com.nbu.medicalrecord.repository;

import com.nbu.medicalrecord.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByEgn(String egn);

    boolean existsByEgn(String egn);

    Optional<Patient> findByUserId(Long userId);

    // Patients with a given diagnosis
    @Query("SELECT DISTINCT p FROM Patient p " +
            "JOIN p.examinations e " +
            "JOIN e.diagnosis d " +
            "WHERE d.id = :diagnosisId")
    List<Patient> findByDiagnosisId(@Param("diagnosisId") Long diagnosisId);

    // Patients registered with a given GP
    List<Patient> findByGpId(Long gpId);

    // Count patients per GP
    @Query("SELECT p.gp.id, COUNT(p) FROM Patient p WHERE p.gp IS NOT NULL GROUP BY p.gp.id")
    List<Object[]> countPatientsByGp();
}
