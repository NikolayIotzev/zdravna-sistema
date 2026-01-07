package com.nbu.medicalrecord.repository;

import com.nbu.medicalrecord.entity.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {

    Optional<Diagnosis> findByCode(String code);

    boolean existsByCode(String code);

    List<Diagnosis> findByNameContainingIgnoreCase(String name);

    // Most frequently diagnosed diagnosis/diagnoses
    @Query("SELECT d, COUNT(e) as cnt FROM Diagnosis d " +
            "JOIN d.examinations e " +
            "GROUP BY d " +
            "ORDER BY cnt DESC")
    List<Object[]> findMostFrequentDiagnoses();
}
