package com.nbu.medicalrecord.service;

import com.nbu.medicalrecord.dto.PatientDto;

import java.util.List;

public interface PatientService {

    PatientDto.Response create(PatientDto.Request request);

    PatientDto.Response getById(Long id);

    PatientDto.Response getByEgn(String egn);

    List<PatientDto.Response> getAll();

    PatientDto.Response update(Long id, PatientDto.Request request);

    void delete(Long id);

    PatientDto.Response getByUserId(Long userId);

    // Reports
    List<PatientDto.Response> getPatientsByDiagnosis(Long diagnosisId);

    List<PatientDto.Response> getPatientsByGp(Long gpId);
}
