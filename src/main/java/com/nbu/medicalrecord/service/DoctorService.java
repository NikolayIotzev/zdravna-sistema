package com.nbu.medicalrecord.service;

import com.nbu.medicalrecord.dto.DoctorDto;
import com.nbu.medicalrecord.dto.ReportDto;

import java.util.List;

public interface DoctorService {

    DoctorDto.Response create(DoctorDto.Request request);

    DoctorDto.Response getById(Long id);

    DoctorDto.Response getByUin(String uin);

    List<DoctorDto.Response> getAll();

    List<DoctorDto.Response> getAllGps();

    DoctorDto.Response update(Long id, DoctorDto.Request request);

    void delete(Long id);

    DoctorDto.Response getByUserId(Long userId);

    // Reports
    List<ReportDto.DoctorPatientCount> getPatientCountPerGp();

    List<ReportDto.DoctorExaminationCount> getExaminationCountPerDoctor();

    List<ReportDto.DoctorSickLeaveCount> getDoctorsWithMostSickLeaves();
}
