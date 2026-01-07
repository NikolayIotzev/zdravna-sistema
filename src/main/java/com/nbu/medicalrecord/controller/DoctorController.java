package com.nbu.medicalrecord.controller;

import com.nbu.medicalrecord.dto.DoctorDto;
import com.nbu.medicalrecord.dto.ReportDto;
import com.nbu.medicalrecord.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorDto.Response> create(@Valid @RequestBody DoctorDto.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorDto.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getById(id));
    }

    @GetMapping("/uin/{uin}")
    public ResponseEntity<DoctorDto.Response> getByUin(@PathVariable String uin) {
        return ResponseEntity.ok(doctorService.getByUin(uin));
    }

    @GetMapping
    public ResponseEntity<List<DoctorDto.Response>> getAll() {
        return ResponseEntity.ok(doctorService.getAll());
    }

    @GetMapping("/gp")
    public ResponseEntity<List<DoctorDto.Response>> getAllGps() {
        return ResponseEntity.ok(doctorService.getAllGps());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorDto.Response> update(@PathVariable Long id, @Valid @RequestBody DoctorDto.Request request) {
        return ResponseEntity.ok(doctorService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        doctorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Reports
    @GetMapping("/reports/patient-count")
    public ResponseEntity<List<ReportDto.DoctorPatientCount>> getPatientCountPerGp() {
        return ResponseEntity.ok(doctorService.getPatientCountPerGp());
    }

    @GetMapping("/reports/examination-count")
    public ResponseEntity<List<ReportDto.DoctorExaminationCount>> getExaminationCountPerDoctor() {
        return ResponseEntity.ok(doctorService.getExaminationCountPerDoctor());
    }

    @GetMapping("/reports/sick-leave-count")
    public ResponseEntity<List<ReportDto.DoctorSickLeaveCount>> getDoctorsWithMostSickLeaves() {
        return ResponseEntity.ok(doctorService.getDoctorsWithMostSickLeaves());
    }
}
