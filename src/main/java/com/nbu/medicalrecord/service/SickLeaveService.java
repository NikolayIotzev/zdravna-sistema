package com.nbu.medicalrecord.service;

import com.nbu.medicalrecord.dto.ReportDto;
import com.nbu.medicalrecord.dto.SickLeaveDto;

import java.util.List;

public interface SickLeaveService {

    SickLeaveDto.Response getById(Long id);

    List<SickLeaveDto.Response> getAll();

    SickLeaveDto.Response getByExaminationId(Long examinationId);

    void delete(Long id);

    // Reports
    List<ReportDto.MonthSickLeaveCount> getMonthsWithMostSickLeaves();
}
