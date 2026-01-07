package com.nbu.medicalrecord.service.impl;

import com.nbu.medicalrecord.dto.*;
import com.nbu.medicalrecord.entity.*;
import com.nbu.medicalrecord.exception.ResourceNotFoundException;
import com.nbu.medicalrecord.repository.*;
import com.nbu.medicalrecord.service.ExaminationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExaminationServiceImpl implements ExaminationService {

    private final ExaminationRepository examinationRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final UserRepository userRepository;

    @Override
    public ExaminationDto.Response create(ExaminationDto.Request request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Пациент с ID " + request.getPatientId() + " не е намерен"));

        Doctor doctor = resolveDoctor(request.getDoctorId());

        Diagnosis diagnosis = null;
        if (request.getDiagnosisId() != null) {
            diagnosis = diagnosisRepository.findById(request.getDiagnosisId())
                    .orElseThrow(() -> new ResourceNotFoundException("Диагноза с ID " + request.getDiagnosisId() + " не е намерена"));
        }

        Examination examination = Examination.builder()
                .examinationDate(request.getExaminationDate())
                .patient(patient)
                .doctor(doctor)
                .diagnosis(diagnosis)
                .treatment(request.getTreatment())
                .prescription(request.getPrescription())
                .build();

        examination = examinationRepository.save(examination);

        // Create sick leave if provided
        if (request.getSickLeave() != null) {
            SickLeave sickLeave = SickLeave.builder()
                    .startDate(request.getSickLeave().getStartDate())
                    .numberOfDays(request.getSickLeave().getNumberOfDays())
                    .examination(examination)
                    .build();
            examination.setSickLeave(sickLeave);
            examination = examinationRepository.save(examination);
        }

        return toResponse(examination);
    }

    @Override
    @Transactional(readOnly = true)
    public ExaminationDto.Response getById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExaminationDto.Response> getAll() {
        return examinationRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ExaminationDto.Response update(Long id, ExaminationDto.Request request) {
        Examination examination = findById(id);

        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Пациент с ID " + request.getPatientId() + " не е намерен"));

        // Doctor cannot be changed - ignore doctorId from request

        Diagnosis diagnosis = null;
        if (request.getDiagnosisId() != null) {
            diagnosis = diagnosisRepository.findById(request.getDiagnosisId())
                    .orElseThrow(() -> new ResourceNotFoundException("Диагноза с ID " + request.getDiagnosisId() + " не е намерена"));
        }

        examination.setExaminationDate(request.getExaminationDate());
        examination.setPatient(patient);
        examination.setDiagnosis(diagnosis);
        examination.setTreatment(request.getTreatment());
        examination.setPrescription(request.getPrescription());

        // Update sick leave
        if (request.getSickLeave() != null) {
            if (examination.getSickLeave() != null) {
                examination.getSickLeave().setStartDate(request.getSickLeave().getStartDate());
                examination.getSickLeave().setNumberOfDays(request.getSickLeave().getNumberOfDays());
            } else {
                SickLeave sickLeave = SickLeave.builder()
                        .startDate(request.getSickLeave().getStartDate())
                        .numberOfDays(request.getSickLeave().getNumberOfDays())
                        .examination(examination)
                        .build();
                examination.setSickLeave(sickLeave);
            }
        }

        return toResponse(examinationRepository.save(examination));
    }

    @Override
    public void delete(Long id) {
        Examination examination = findById(id);
        examinationRepository.delete(examination);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExaminationDto.Response> getByPatientId(Long patientId) {
        return examinationRepository.findByPatientId(patientId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExaminationDto.Response> getByDoctorId(Long doctorId) {
        return examinationRepository.findByDoctorId(doctorId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExaminationDto.Response> getAllInPeriod(LocalDate startDate, LocalDate endDate) {
        return examinationRepository.findAllInPeriod(startDate, endDate).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExaminationDto.Response> getByDoctorInPeriod(Long doctorId, LocalDate startDate, LocalDate endDate) {
        return examinationRepository.findByDoctorInPeriod(doctorId, startDate, endDate).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportDto.PatientExaminations> getExaminationsGroupedByPatient() {
        List<Examination> allExaminations = examinationRepository.findAll();

        Map<Patient, List<Examination>> grouped = allExaminations.stream()
                .collect(Collectors.groupingBy(Examination::getPatient));

        return grouped.entrySet().stream()
                .map(entry -> ReportDto.PatientExaminations.builder()
                        .patient(toPatientSummary(entry.getKey()))
                        .examinations(entry.getValue().stream()
                                .map(this::toResponse)
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    private Examination findById(Long id) {
        return examinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Преглед с ID " + id + " не е намерен"));
    }

    private ExaminationDto.Response toResponse(Examination examination) {
        DiagnosisDto.Response diagnosisResponse = null;
        if (examination.getDiagnosis() != null) {
            diagnosisResponse = DiagnosisDto.Response.builder()
                    .id(examination.getDiagnosis().getId())
                    .code(examination.getDiagnosis().getCode())
                    .name(examination.getDiagnosis().getName())
                    .description(examination.getDiagnosis().getDescription())
                    .build();
        }

        SickLeaveDto.Response sickLeaveResponse = null;
        if (examination.getSickLeave() != null) {
            sickLeaveResponse = SickLeaveDto.Response.builder()
                    .id(examination.getSickLeave().getId())
                    .startDate(examination.getSickLeave().getStartDate())
                    .numberOfDays(examination.getSickLeave().getNumberOfDays())
                    .endDate(examination.getSickLeave().getEndDate())
                    .build();
        }

        return ExaminationDto.Response.builder()
                .id(examination.getId())
                .examinationDate(examination.getExaminationDate())
                .patient(toPatientSummary(examination.getPatient()))
                .doctor(toDoctorSummary(examination.getDoctor()))
                .diagnosis(diagnosisResponse)
                .treatment(examination.getTreatment())
                .prescription(examination.getPrescription())
                .sickLeave(sickLeaveResponse)
                .build();
    }

    private PatientDto.Summary toPatientSummary(Patient patient) {
        return PatientDto.Summary.builder()
                .id(patient.getId())
                .name(patient.getName())
                .egn(patient.getEgn())
                .hasValidInsurance(patient.hasValidInsurance())
                .build();
    }

    private DoctorDto.Summary toDoctorSummary(Doctor doctor) {
        return DoctorDto.Summary.builder()
                .id(doctor.getId())
                .uin(doctor.getUin())
                .name(doctor.getName())
                .isGp(doctor.isGp())
                .build();
    }

    /**
     * Resolves the doctor for an examination.
     * If doctorId is provided (by admin), use that.
     * If not provided, use the currently logged-in doctor.
     */
    private Doctor resolveDoctor(Long doctorId) {
        if (doctorId != null) {
            return doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Лекар с ID " + doctorId + " не е намерен"));
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new IllegalStateException("Няма автентикиран потребител");
        }

        return userRepository.findByUsername(auth.getName())
                .map(User::getDoctor)
                .orElseThrow(() -> new IllegalStateException("Текущият потребител не е лекар"));
    }
}
