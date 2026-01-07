package com.nbu.medicalrecord.controller;

import com.nbu.medicalrecord.dto.SpecialtyDto;
import com.nbu.medicalrecord.service.SpecialtyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specialties")
@RequiredArgsConstructor
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SpecialtyDto.Response> create(@Valid @RequestBody SpecialtyDto.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(specialtyService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpecialtyDto.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(specialtyService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<SpecialtyDto.Response>> getAll() {
        return ResponseEntity.ok(specialtyService.getAll());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SpecialtyDto.Response> update(@PathVariable Long id, @Valid @RequestBody SpecialtyDto.Request request) {
        return ResponseEntity.ok(specialtyService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        specialtyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
