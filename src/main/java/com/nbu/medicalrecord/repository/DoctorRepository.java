package com.nbu.medicalrecord.repository;

import com.nbu.medicalrecord.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByUin(String uin);

    boolean existsByUin(String uin);

    List<Doctor> findByIsGpTrue();

    Optional<Doctor> findByUserId(Long userId);

    // Count of patients registered with each GP
    @Query("SELECT d, COUNT(p) FROM Doctor d LEFT JOIN d.patients p WHERE d.isGp = true GROUP BY d")
    List<Object[]> countPatientsPerGp();

    // Count of visits (examinations) per doctor
    @Query("SELECT d, COUNT(e) FROM Doctor d LEFT JOIN d.examinations e GROUP BY d")
    List<Object[]> countExaminationsPerDoctor();

    // Doctors who issued most sick leaves
    @Query("SELECT d, COUNT(sl) as cnt FROM Doctor d " +
            "JOIN d.examinations e " +
            "JOIN e.sickLeave sl " +
            "GROUP BY d " +
            "ORDER BY cnt DESC")
    List<Object[]> findDoctorsWithMostSickLeaves();

    @Query("SELECT d FROM Doctor d JOIN d.specialties s WHERE s.id = :specialtyId")
    List<Doctor> findBySpecialtyId(@Param("specialtyId") Long specialtyId);
}
