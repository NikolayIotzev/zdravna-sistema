package com.nbu.medicalrecord.service;

import com.nbu.medicalrecord.dto.ExaminationDto;
import com.nbu.medicalrecord.dto.ReportDto;

import java.time.LocalDate;
import java.util.List;

public interface ExaminationService {

    ExaminationDto.Response create(ExaminationDto.Request request);

    ExaminationDto.Response getById(Long id);

    List<ExaminationDto.Response> getAll();

    ExaminationDto.Response update(Long id, ExaminationDto.Request request);

    void delete(Long id);

    // Patient examinations
    List<ExaminationDto.Response> getByPatientId(Long patientId);

    // Doctor examinations
    List<ExaminationDto.Response> getByDoctorId(Long doctorId);

    // Reports
    List<ExaminationDto.Response> getAllInPeriod(LocalDate startDate, LocalDate endDate);

    List<ExaminationDto.Response> getByDoctorInPeriod(Long doctorId, LocalDate startDate, LocalDate endDate);

    List<ReportDto.PatientExaminations> getExaminationsGroupedByPatient();
}
