package com.nbu.medicalrecord.repository;

import com.nbu.medicalrecord.entity.SickLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SickLeaveRepository extends JpaRepository<SickLeave, Long> {

    Optional<SickLeave> findByExaminationId(Long examinationId);

    // Month(s) with most sick leaves issued
    @Query("SELECT MONTH(sl.startDate), YEAR(sl.startDate), COUNT(sl) as cnt " +
            "FROM SickLeave sl " +
            "GROUP BY MONTH(sl.startDate), YEAR(sl.startDate) " +
            "ORDER BY cnt DESC")
    List<Object[]> findMonthsWithMostSickLeaves();

    // Count sick leaves by doctor
    @Query("SELECT e.doctor.id, COUNT(sl) FROM SickLeave sl JOIN sl.examination e GROUP BY e.doctor.id")
    List<Object[]> countSickLeavesByDoctor();

    // Find sick leaves by patient
    @Query("SELECT sl FROM SickLeave sl WHERE sl.examination.patient.id = :patientId")
    List<SickLeave> findByPatientId(Long patientId);
}
