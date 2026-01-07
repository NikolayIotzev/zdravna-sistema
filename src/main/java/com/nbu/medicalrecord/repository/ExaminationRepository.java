package com.nbu.medicalrecord.repository;

import com.nbu.medicalrecord.entity.Examination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExaminationRepository extends JpaRepository<Examination, Long> {

    // Examinations by patient
    List<Examination> findByPatientId(Long patientId);

    // Examinations by doctor
    List<Examination> findByDoctorId(Long doctorId);

    // Examinations for all doctors in a given period
    @Query("SELECT e FROM Examination e WHERE e.examinationDate BETWEEN :startDate AND :endDate ORDER BY e.doctor.id, e.examinationDate")
    List<Examination> findAllInPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Examinations for a specific doctor in a given period
    @Query("SELECT e FROM Examination e WHERE e.doctor.id = :doctorId AND e.examinationDate BETWEEN :startDate AND :endDate ORDER BY e.examinationDate")
    List<Examination> findByDoctorInPeriod(@Param("doctorId") Long doctorId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

    // Count examinations by patient
    @Query("SELECT e.patient.id, COUNT(e) FROM Examination e GROUP BY e.patient.id")
    List<Object[]> countExaminationsByPatient();

    // Check if doctor has examined patient
    boolean existsByDoctorIdAndPatientId(Long doctorId, Long patientId);
}
