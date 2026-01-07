package com.nbu.medicalrecord.service;

import com.nbu.medicalrecord.dto.DiagnosisDto;
import com.nbu.medicalrecord.dto.ReportDto;

import java.util.List;

public interface DiagnosisService {

    DiagnosisDto.Response create(DiagnosisDto.Request request);

    DiagnosisDto.Response getById(Long id);

    DiagnosisDto.Response getByCode(String code);

    List<DiagnosisDto.Response> getAll();

    List<DiagnosisDto.Response> searchByName(String name);

    DiagnosisDto.Response update(Long id, DiagnosisDto.Request request);

    void delete(Long id);

    // Reports
    List<ReportDto.DiagnosisFrequency> getMostFrequentDiagnoses();
}
