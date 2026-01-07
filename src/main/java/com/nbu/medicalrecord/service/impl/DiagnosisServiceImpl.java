package com.nbu.medicalrecord.service.impl;

import com.nbu.medicalrecord.dto.DiagnosisDto;
import com.nbu.medicalrecord.dto.ReportDto;
import com.nbu.medicalrecord.entity.Diagnosis;
import com.nbu.medicalrecord.exception.DuplicateResourceException;
import com.nbu.medicalrecord.exception.ResourceNotFoundException;
import com.nbu.medicalrecord.repository.DiagnosisRepository;
import com.nbu.medicalrecord.service.DiagnosisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DiagnosisServiceImpl implements DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;

    @Override
    public DiagnosisDto.Response create(DiagnosisDto.Request request) {
        if (diagnosisRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Диагноза с код '" + request.getCode() + "' вече съществува");
        }

        Diagnosis diagnosis = Diagnosis.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .build();

        return toResponse(diagnosisRepository.save(diagnosis));
    }

    @Override
    @Transactional(readOnly = true)
    public DiagnosisDto.Response getById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public DiagnosisDto.Response getByCode(String code) {
        return toResponse(diagnosisRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Диагноза с код '" + code + "' не е намерена")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiagnosisDto.Response> getAll() {
        return diagnosisRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiagnosisDto.Response> searchByName(String name) {
        return diagnosisRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DiagnosisDto.Response update(Long id, DiagnosisDto.Request request) {
        Diagnosis diagnosis = findById(id);

        if (!diagnosis.getCode().equals(request.getCode()) &&
                diagnosisRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Диагноза с код '" + request.getCode() + "' вече съществува");
        }

        diagnosis.setCode(request.getCode());
        diagnosis.setName(request.getName());
        diagnosis.setDescription(request.getDescription());

        return toResponse(diagnosisRepository.save(diagnosis));
    }

    @Override
    public void delete(Long id) {
        Diagnosis diagnosis = findById(id);
        diagnosisRepository.delete(diagnosis);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDto.DiagnosisFrequency> getMostFrequentDiagnoses() {
        return diagnosisRepository.findMostFrequentDiagnoses().stream()
                .map(row -> ReportDto.DiagnosisFrequency.builder()
                        .diagnosis(toResponse((Diagnosis) row[0]))
                        .frequency((Long) row[1])
                        .build())
                .collect(Collectors.toList());
    }

    private Diagnosis findById(Long id) {
        return diagnosisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Диагноза с ID " + id + " не е намерена"));
    }

    private DiagnosisDto.Response toResponse(Diagnosis diagnosis) {
        return DiagnosisDto.Response.builder()
                .id(diagnosis.getId())
                .code(diagnosis.getCode())
                .name(diagnosis.getName())
                .description(diagnosis.getDescription())
                .build();
    }
}
