package com.nbu.medicalrecord.service;

import com.nbu.medicalrecord.dto.DoctorDto;
import com.nbu.medicalrecord.entity.Doctor;
import com.nbu.medicalrecord.exception.DuplicateResourceException;
import com.nbu.medicalrecord.exception.ResourceNotFoundException;
import com.nbu.medicalrecord.repository.DoctorRepository;
import com.nbu.medicalrecord.repository.SpecialtyRepository;
import com.nbu.medicalrecord.service.impl.DoctorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    private Doctor doctor;
    private DoctorDto.Request request;

    @BeforeEach
    void setUp() {
        doctor = Doctor.builder()
                .id(1L)
                .uin("1234567890")
                .name("Д-р Иван Петров")
                .isGp(true)
                .specialties(new HashSet<>())
                .patients(new HashSet<>())
                .build();

        request = DoctorDto.Request.builder()
                .uin("1234567890")
                .name("Д-р Иван Петров")
                .isGp(true)
                .build();
    }

    @Test
    void create_WithValidData_ShouldReturnDoctor() {
        when(doctorRepository.existsByUin(request.getUin())).thenReturn(false);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        DoctorDto.Response result = doctorService.create(request);

        assertNotNull(result);
        assertEquals(doctor.getUin(), result.getUin());
        assertEquals(doctor.getName(), result.getName());
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    void create_WithDuplicateUin_ShouldThrowException() {
        when(doctorRepository.existsByUin(request.getUin())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> doctorService.create(request));
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void getById_WithValidId_ShouldReturnDoctor() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        DoctorDto.Response result = doctorService.getById(1L);

        assertNotNull(result);
        assertEquals(doctor.getId(), result.getId());
    }

    @Test
    void getById_WithInvalidId_ShouldThrowException() {
        when(doctorRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> doctorService.getById(999L));
    }

    @Test
    void getAll_ShouldReturnAllDoctors() {
        when(doctorRepository.findAll()).thenReturn(Collections.singletonList(doctor));

        var result = doctorService.getAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void delete_WithValidId_ShouldDeleteDoctor() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        doNothing().when(doctorRepository).delete(doctor);

        doctorService.delete(1L);

        verify(doctorRepository).delete(doctor);
    }
}
