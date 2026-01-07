package com.nbu.medicalrecord.service.impl;

import com.nbu.medicalrecord.dto.ReportDto;
import com.nbu.medicalrecord.dto.SickLeaveDto;
import com.nbu.medicalrecord.entity.SickLeave;
import com.nbu.medicalrecord.exception.ResourceNotFoundException;
import com.nbu.medicalrecord.repository.SickLeaveRepository;
import com.nbu.medicalrecord.service.SickLeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SickLeaveServiceImpl implements SickLeaveService {

    private final SickLeaveRepository sickLeaveRepository;

    @Override
    @Transactional(readOnly = true)
    public SickLeaveDto.Response getById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SickLeaveDto.Response> getAll() {
        return sickLeaveRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SickLeaveDto.Response getByExaminationId(Long examinationId) {
        return toResponse(sickLeaveRepository.findByExaminationId(examinationId)
                .orElseThrow(() -> new ResourceNotFoundException("Болничен лист за преглед с ID " + examinationId + " не е намерен")));
    }

    @Override
    public void delete(Long id) {
        SickLeave sickLeave = findById(id);
        sickLeaveRepository.delete(sickLeave);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDto.MonthSickLeaveCount> getMonthsWithMostSickLeaves() {
        return sickLeaveRepository.findMonthsWithMostSickLeaves().stream()
                .map(row -> ReportDto.MonthSickLeaveCount.builder()
                        .month((Integer) row[0])
                        .year((Integer) row[1])
                        .count((Long) row[2])
                        .build())
                .collect(Collectors.toList());
    }

    private SickLeave findById(Long id) {
        return sickLeaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Болничен лист с ID " + id + " не е намерен"));
    }

    private SickLeaveDto.Response toResponse(SickLeave sickLeave) {
        return SickLeaveDto.Response.builder()
                .id(sickLeave.getId())
                .startDate(sickLeave.getStartDate())
                .numberOfDays(sickLeave.getNumberOfDays())
                .endDate(sickLeave.getEndDate())
                .build();
    }
}
