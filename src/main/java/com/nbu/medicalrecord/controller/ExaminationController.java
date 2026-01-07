package com.nbu.medicalrecord.controller;

import com.nbu.medicalrecord.dto.ExaminationDto;
import com.nbu.medicalrecord.dto.ReportDto;
import com.nbu.medicalrecord.service.ExaminationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/examinations")
@RequiredArgsConstructor
public class ExaminationController {

    private final ExaminationService examinationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ExaminationDto.Response> create(@Valid @RequestBody ExaminationDto.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(examinationService.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR') or @securityService.canAccessExamination(#id, authentication)")
    public ResponseEntity<ExaminationDto.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(examinationService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<ExaminationDto.Response>> getAll() {
        return ResponseEntity.ok(examinationService.getAll());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isDoctorForExamination(#id, authentication)")
    public ResponseEntity<ExaminationDto.Response> update(@PathVariable Long id, @Valid @RequestBody ExaminationDto.Request request) {
        return ResponseEntity.ok(examinationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        examinationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR') or @securityService.isOwnerPatient(#patientId, authentication)")
    public ResponseEntity<List<ExaminationDto.Response>> getByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(examinationService.getByPatientId(patientId));
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<ExaminationDto.Response>> getByDoctorId(@PathVariable Long doctorId) {
        return ResponseEntity.ok(examinationService.getByDoctorId(doctorId));
    }

    // Reports
    @GetMapping("/reports/by-period")
    public ResponseEntity<List<ExaminationDto.Response>> getAllInPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(examinationService.getAllInPeriod(startDate, endDate));
    }

    @GetMapping("/reports/by-doctor-period/{doctorId}")
    public ResponseEntity<List<ExaminationDto.Response>> getByDoctorInPeriod(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(examinationService.getByDoctorInPeriod(doctorId, startDate, endDate));
    }

    @GetMapping("/reports/by-patient")
    public ResponseEntity<List<ReportDto.PatientExaminations>> getExaminationsGroupedByPatient() {
        return ResponseEntity.ok(examinationService.getExaminationsGroupedByPatient());
    }
}
