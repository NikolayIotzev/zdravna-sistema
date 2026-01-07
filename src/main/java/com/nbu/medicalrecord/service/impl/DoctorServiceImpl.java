package com.nbu.medicalrecord.service.impl;

import com.nbu.medicalrecord.dto.DoctorDto;
import com.nbu.medicalrecord.dto.ReportDto;
import com.nbu.medicalrecord.dto.SpecialtyDto;
import com.nbu.medicalrecord.entity.Doctor;
import com.nbu.medicalrecord.entity.Specialty;
import com.nbu.medicalrecord.exception.DuplicateResourceException;
import com.nbu.medicalrecord.exception.ResourceNotFoundException;
import com.nbu.medicalrecord.repository.DoctorRepository;
import com.nbu.medicalrecord.repository.SpecialtyRepository;
import com.nbu.medicalrecord.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;

    @Override
    public DoctorDto.Response create(DoctorDto.Request request) {
        if (doctorRepository.existsByUin(request.getUin())) {
            throw new DuplicateResourceException("Лекар с УИН '" + request.getUin() + "' вече съществува");
        }

        Set<Specialty> specialties = new HashSet<>();
        if (request.getSpecialtyIds() != null && !request.getSpecialtyIds().isEmpty()) {
            specialties = request.getSpecialtyIds().stream()
                    .map(id -> specialtyRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Специалност с ID " + id + " не е намерена")))
                    .collect(Collectors.toSet());
        }

        Doctor doctor = Doctor.builder()
                .uin(request.getUin())
                .name(request.getName())
                .isGp(request.isGp())
                .specialties(specialties)
                .build();

        return toResponse(doctorRepository.save(doctor));
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorDto.Response getById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorDto.Response getByUin(String uin) {
        return toResponse(doctorRepository.findByUin(uin)
                .orElseThrow(() -> new ResourceNotFoundException("Лекар с УИН '" + uin + "' не е намерен")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorDto.Response> getAll() {
        return doctorRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorDto.Response> getAllGps() {
        return doctorRepository.findByIsGpTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DoctorDto.Response update(Long id, DoctorDto.Request request) {
        Doctor doctor = findById(id);

        if (!doctor.getUin().equals(request.getUin()) &&
                doctorRepository.existsByUin(request.getUin())) {
            throw new DuplicateResourceException("Лекар с УИН '" + request.getUin() + "' вече съществува");
        }

        Set<Specialty> specialties = new HashSet<>();
        if (request.getSpecialtyIds() != null && !request.getSpecialtyIds().isEmpty()) {
            specialties = request.getSpecialtyIds().stream()
                    .map(specId -> specialtyRepository.findById(specId)
                            .orElseThrow(() -> new ResourceNotFoundException("Специалност с ID " + specId + " не е намерена")))
                    .collect(Collectors.toSet());
        }

        doctor.setUin(request.getUin());
        doctor.setName(request.getName());
        doctor.setGp(request.isGp());
        doctor.setSpecialties(specialties);

        return toResponse(doctorRepository.save(doctor));
    }

    @Override
    public void delete(Long id) {
        Doctor doctor = findById(id);
        doctorRepository.delete(doctor);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorDto.Response getByUserId(Long userId) {
        return doctorRepository.findByUserId(userId)
                .map(this::toResponse)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDto.DoctorPatientCount> getPatientCountPerGp() {
        return doctorRepository.countPatientsPerGp().stream()
                .map(row -> ReportDto.DoctorPatientCount.builder()
                        .doctor(toSummary((Doctor) row[0]))
                        .patientCount((Long) row[1])
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDto.DoctorExaminationCount> getExaminationCountPerDoctor() {
        return doctorRepository.countExaminationsPerDoctor().stream()
                .map(row -> ReportDto.DoctorExaminationCount.builder()
                        .doctor(toSummary((Doctor) row[0]))
                        .examinationCount((Long) row[1])
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDto.DoctorSickLeaveCount> getDoctorsWithMostSickLeaves() {
        return doctorRepository.findDoctorsWithMostSickLeaves().stream()
                .map(row -> ReportDto.DoctorSickLeaveCount.builder()
                        .doctor(toSummary((Doctor) row[0]))
                        .sickLeaveCount((Long) row[1])
                        .build())
                .collect(Collectors.toList());
    }

    private Doctor findById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Лекар с ID " + id + " не е намерен"));
    }

    private DoctorDto.Response toResponse(Doctor doctor) {
        return DoctorDto.Response.builder()
                .id(doctor.getId())
                .uin(doctor.getUin())
                .name(doctor.getName())
                .isGp(doctor.isGp())
                .specialties(doctor.getSpecialties().stream()
                        .map(s -> SpecialtyDto.Response.builder()
                                .id(s.getId())
                                .name(s.getName())
                                .build())
                        .collect(Collectors.toSet()))
                .patientCount(doctor.getPatients() != null ? doctor.getPatients().size() : 0)
                .build();
    }

    private DoctorDto.Summary toSummary(Doctor doctor) {
        return DoctorDto.Summary.builder()
                .id(doctor.getId())
                .uin(doctor.getUin())
                .name(doctor.getName())
                .isGp(doctor.isGp())
                .build();
    }
}
