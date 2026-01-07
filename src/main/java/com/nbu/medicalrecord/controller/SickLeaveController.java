package com.nbu.medicalrecord.controller;

import com.nbu.medicalrecord.dto.ReportDto;
import com.nbu.medicalrecord.dto.SickLeaveDto;
import com.nbu.medicalrecord.service.SickLeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sick-leaves")
@RequiredArgsConstructor
public class SickLeaveController {

    private final SickLeaveService sickLeaveService;

    @GetMapping("/{id}")
    public ResponseEntity<SickLeaveDto.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(sickLeaveService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<SickLeaveDto.Response>> getAll() {
        return ResponseEntity.ok(sickLeaveService.getAll());
    }

    @GetMapping("/examination/{examinationId}")
    public ResponseEntity<SickLeaveDto.Response> getByExaminationId(@PathVariable Long examinationId) {
        return ResponseEntity.ok(sickLeaveService.getByExaminationId(examinationId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sickLeaveService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Reports
    @GetMapping("/reports/by-month")
    public ResponseEntity<List<ReportDto.MonthSickLeaveCount>> getMonthsWithMostSickLeaves() {
        return ResponseEntity.ok(sickLeaveService.getMonthsWithMostSickLeaves());
    }
}
