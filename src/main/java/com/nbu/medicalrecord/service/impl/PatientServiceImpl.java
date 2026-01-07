package com.nbu.medicalrecord.service.impl;

import com.nbu.medicalrecord.dto.DoctorDto;
import com.nbu.medicalrecord.dto.PatientDto;
import com.nbu.medicalrecord.entity.Doctor;
import com.nbu.medicalrecord.entity.Patient;
import com.nbu.medicalrecord.exception.DuplicateResourceException;
import com.nbu.medicalrecord.exception.ResourceNotFoundException;
import com.nbu.medicalrecord.exception.ValidationException;
import com.nbu.medicalrecord.repository.DoctorRepository;
import com.nbu.medicalrecord.repository.PatientRepository;
import com.nbu.medicalrecord.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Override
    public PatientDto.Response create(PatientDto.Request request) {
        if (patientRepository.existsByEgn(request.getEgn())) {
            throw new DuplicateResourceException("Пациент с ЕГН '" + request.getEgn() + "' вече съществува");
        }

        Doctor gp = null;
        if (request.getGpId() != null) {
            gp = doctorRepository.findById(request.getGpId())
                    .orElseThrow(() -> new ResourceNotFoundException("Лекар с ID " + request.getGpId() + " не е намерен"));
            if (!gp.isGp()) {
                throw new ValidationException("Избраният лекар не е личен лекар (GP)");
            }
        }

        Patient patient = Patient.builder()
                .name(request.getName())
                .egn(request.getEgn())
                .lastInsurancePayment(request.getLastInsurancePayment())
                .gp(gp)
                .build();

        return toResponse(patientRepository.save(patient));
    }

    @Override
    @Transactional(readOnly = true)
    public PatientDto.Response getById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PatientDto.Response getByEgn(String egn) {
        return toResponse(patientRepository.findByEgn(egn)
                .orElseThrow(() -> new ResourceNotFoundException("Пациент с ЕГН '" + egn + "' не е намерен")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientDto.Response> getAll() {
        return patientRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PatientDto.Response update(Long id, PatientDto.Request request) {
        Patient patient = findById(id);

        if (!patient.getEgn().equals(request.getEgn()) &&
                patientRepository.existsByEgn(request.getEgn())) {
            throw new DuplicateResourceException("Пациент с ЕГН '" + request.getEgn() + "' вече съществува");
        }

        Doctor gp = null;
        if (request.getGpId() != null) {
            gp = doctorRepository.findById(request.getGpId())
                    .orElseThrow(() -> new ResourceNotFoundException("Лекар с ID " + request.getGpId() + " не е намерен"));
            if (!gp.isGp()) {
                throw new ValidationException("Избраният лекар не е личен лекар (GP)");
            }
        }

        patient.setName(request.getName());
        patient.setEgn(request.getEgn());
        patient.setLastInsurancePayment(request.getLastInsurancePayment());
        patient.setGp(gp);

        return toResponse(patientRepository.save(patient));
    }

    @Override
    public void delete(Long id) {
        Patient patient = findById(id);
        patientRepository.delete(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientDto.Response getByUserId(Long userId) {
        return patientRepository.findByUserId(userId)
                .map(this::toResponse)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientDto.Response> getPatientsByDiagnosis(Long diagnosisId) {
        return patientRepository.findByDiagnosisId(diagnosisId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientDto.Response> getPatientsByGp(Long gpId) {
        return patientRepository.findByGpId(gpId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private Patient findById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пациент с ID " + id + " не е намерен"));
    }

    private PatientDto.Response toResponse(Patient patient) {
        DoctorDto.Summary gpSummary = null;
        if (patient.getGp() != null) {
            gpSummary = DoctorDto.Summary.builder()
                    .id(patient.getGp().getId())
                    .uin(patient.getGp().getUin())
                    .name(patient.getGp().getName())
                    .isGp(patient.getGp().isGp())
                    .build();
        }

        return PatientDto.Response.builder()
                .id(patient.getId())
                .name(patient.getName())
                .egn(patient.getEgn())
                .lastInsurancePayment(patient.getLastInsurancePayment())
                .hasValidInsurance(patient.hasValidInsurance())
                .gp(gpSummary)
                .build();
    }
}
