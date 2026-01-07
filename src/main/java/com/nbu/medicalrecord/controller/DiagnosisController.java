package com.nbu.medicalrecord.controller;

import com.nbu.medicalrecord.dto.DiagnosisDto;
import com.nbu.medicalrecord.dto.ReportDto;
import com.nbu.medicalrecord.service.DiagnosisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diagnoses")
@RequiredArgsConstructor
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DiagnosisDto.Response> create(@Valid @RequestBody DiagnosisDto.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(diagnosisService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiagnosisDto.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(diagnosisService.getById(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<DiagnosisDto.Response> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(diagnosisService.getByCode(code));
    }

    @GetMapping
    public ResponseEntity<List<DiagnosisDto.Response>> getAll() {
        return ResponseEntity.ok(diagnosisService.getAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<DiagnosisDto.Response>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(diagnosisService.searchByName(name));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DiagnosisDto.Response> update(@PathVariable Long id, @Valid @RequestBody DiagnosisDto.Request request) {
        return ResponseEntity.ok(diagnosisService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        diagnosisService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Reports
    @GetMapping("/reports/most-frequent")
    public ResponseEntity<List<ReportDto.DiagnosisFrequency>> getMostFrequentDiagnoses() {
        return ResponseEntity.ok(diagnosisService.getMostFrequentDiagnoses());
    }
}
