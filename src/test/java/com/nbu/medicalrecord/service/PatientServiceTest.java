package com.nbu.medicalrecord.service;

import com.nbu.medicalrecord.dto.PatientDto;
import com.nbu.medicalrecord.entity.Doctor;
import com.nbu.medicalrecord.entity.Patient;
import com.nbu.medicalrecord.exception.DuplicateResourceException;
import com.nbu.medicalrecord.exception.ResourceNotFoundException;
import com.nbu.medicalrecord.exception.ValidationException;
import com.nbu.medicalrecord.repository.DoctorRepository;
import com.nbu.medicalrecord.repository.PatientRepository;
import com.nbu.medicalrecord.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private PatientServiceImpl patientService;

    private Patient patient;
    private Doctor gp;
    private PatientDto.Request request;

    @BeforeEach
    void setUp() {
        gp = Doctor.builder()
                .id(1L)
                .uin("1234567890")
                .name("Д-р Иван Петров")
                .isGp(true)
                .build();

        patient = Patient.builder()
                .id(1L)
                .name("Георги Димитров")
                .egn("8501011234")
                .lastInsurancePayment(LocalDate.now().minusMonths(2))
                .gp(gp)
                .build();

        request = PatientDto.Request.builder()
                .name("Георги Димитров")
                .egn("8501011234")
                .lastInsurancePayment(LocalDate.now().minusMonths(2))
                .gpId(1L)
                .build();
    }

    @Test
    void create_WithValidData_ShouldReturnPatient() {
        when(patientRepository.existsByEgn(request.getEgn())).thenReturn(false);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(gp));
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        PatientDto.Response result = patientService.create(request);

        assertNotNull(result);
        assertEquals(patient.getName(), result.getName());
        assertEquals(patient.getEgn(), result.getEgn());
        assertTrue(result.isHasValidInsurance());
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void create_WithDuplicateEgn_ShouldThrowException() {
        when(patientRepository.existsByEgn(request.getEgn())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> patientService.create(request));
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void create_WithNonGpDoctor_ShouldThrowValidationException() {
        Doctor specialist = Doctor.builder()
                .id(2L)
                .uin("0987654321")
                .name("Д-р Специалист")
                .isGp(false)
                .build();

        request.setGpId(2L);
        when(patientRepository.existsByEgn(request.getEgn())).thenReturn(false);
        when(doctorRepository.findById(2L)).thenReturn(Optional.of(specialist));

        assertThrows(ValidationException.class, () -> patientService.create(request));
    }

    @Test
    void getById_WithValidId_ShouldReturnPatient() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        PatientDto.Response result = patientService.getById(1L);

        assertNotNull(result);
        assertEquals(patient.getId(), result.getId());
    }

    @Test
    void getById_WithInvalidId_ShouldThrowException() {
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> patientService.getById(999L));
    }

    @Test
    void hasValidInsurance_WithRecentPayment_ShouldReturnTrue() {
        patient.setLastInsurancePayment(LocalDate.now().minusMonths(3));
        assertTrue(patient.hasValidInsurance());
    }

    @Test
    void hasValidInsurance_WithOldPayment_ShouldReturnFalse() {
        patient.setLastInsurancePayment(LocalDate.now().minusMonths(8));
        assertFalse(patient.hasValidInsurance());
    }

    @Test
    void hasValidInsurance_WithNullPayment_ShouldReturnFalse() {
        patient.setLastInsurancePayment(null);
        assertFalse(patient.hasValidInsurance());
    }
}
