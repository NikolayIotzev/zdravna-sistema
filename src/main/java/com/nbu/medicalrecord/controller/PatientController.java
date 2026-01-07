package com.nbu.medicalrecord.controller;

import com.nbu.medicalrecord.dto.PatientDto;
import com.nbu.medicalrecord.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PatientDto.Response> create(@Valid @RequestBody PatientDto.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR') or @securityService.isOwnerPatient(#id, authentication)")
    public ResponseEntity<PatientDto.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getById(id));
    }

    @GetMapping("/egn/{egn}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<PatientDto.Response> getByEgn(@PathVariable String egn) {
        return ResponseEntity.ok(patientService.getByEgn(egn));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<PatientDto.Response>> getAll() {
        return ResponseEntity.ok(patientService.getAll());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PatientDto.Response> update(@PathVariable Long id, @Valid @RequestBody PatientDto.Request request) {
        return ResponseEntity.ok(patientService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        patientService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Reports
    @GetMapping("/by-diagnosis/{diagnosisId}")
    public ResponseEntity<List<PatientDto.Response>> getPatientsByDiagnosis(@PathVariable Long diagnosisId) {
        return ResponseEntity.ok(patientService.getPatientsByDiagnosis(diagnosisId));
    }

    @GetMapping("/by-gp/{gpId}")
    public ResponseEntity<List<PatientDto.Response>> getPatientsByGp(@PathVariable Long gpId) {
        return ResponseEntity.ok(patientService.getPatientsByGp(gpId));
    }
}
