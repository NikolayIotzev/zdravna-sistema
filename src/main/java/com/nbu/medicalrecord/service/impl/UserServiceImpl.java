package com.nbu.medicalrecord.service.impl;

import com.nbu.medicalrecord.dto.DoctorDto;
import com.nbu.medicalrecord.dto.PatientDto;
import com.nbu.medicalrecord.dto.UserDto;
import com.nbu.medicalrecord.entity.*;
import com.nbu.medicalrecord.exception.DuplicateResourceException;
import com.nbu.medicalrecord.exception.ResourceNotFoundException;
import com.nbu.medicalrecord.exception.ValidationException;
import com.nbu.medicalrecord.repository.*;
import com.nbu.medicalrecord.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final SpecialtyRepository specialtyRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto.Response register(UserDto.RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Потребител с име '" + request.getUsername() + "' вече съществува");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        user = userRepository.save(user);

        // Create associated doctor or patient based on role
        if (request.getRole() == User.Role.DOCTOR && request.getDoctor() != null) {
            if (doctorRepository.existsByUin(request.getDoctor().getUin())) {
                throw new DuplicateResourceException("Лекар с УИН '" + request.getDoctor().getUin() + "' вече съществува");
            }

            Set<Specialty> specialties = new HashSet<>();
            if (request.getDoctor().getSpecialtyIds() != null) {
                specialties = request.getDoctor().getSpecialtyIds().stream()
                        .map(id -> specialtyRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Специалност с ID " + id + " не е намерена")))
                        .collect(Collectors.toSet());
            }

            Doctor doctor = Doctor.builder()
                    .uin(request.getDoctor().getUin())
                    .name(request.getDoctor().getName())
                    .isGp(request.getDoctor().isGp())
                    .specialties(specialties)
                    .user(user)
                    .build();

            doctorRepository.save(doctor);
            user.setDoctor(doctor);
        } else if (request.getRole() == User.Role.PATIENT && request.getPatient() != null) {
            if (patientRepository.existsByEgn(request.getPatient().getEgn())) {
                throw new DuplicateResourceException("Пациент с ЕГН '" + request.getPatient().getEgn() + "' вече съществува");
            }

            Doctor gp = null;
            if (request.getPatient().getGpId() != null) {
                gp = doctorRepository.findById(request.getPatient().getGpId())
                        .orElseThrow(() -> new ResourceNotFoundException("Лекар с ID " + request.getPatient().getGpId() + " не е намерен"));
                if (!gp.isGp()) {
                    throw new ValidationException("Избраният лекар не е личен лекар (GP)");
                }
            }

            Patient patient = Patient.builder()
                    .name(request.getPatient().getName())
                    .egn(request.getPatient().getEgn())
                    .lastInsurancePayment(request.getPatient().getLastInsurancePayment())
                    .gp(gp)
                    .user(user)
                    .build();

            patientRepository.save(patient);
            user.setPatient(patient);
        }

        return toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto.Response getById(Long id) {
        return toResponse(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto.Response getByUsername(String username) {
        return toResponse(userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Потребител с име '" + username + "' не е намерен")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto.Response> getAll() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    private User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Потребител с ID " + id + " не е намерен"));
    }

    private UserDto.Response toResponse(User user) {
        DoctorDto.Summary doctorSummary = null;
        if (user.getDoctor() != null) {
            doctorSummary = DoctorDto.Summary.builder()
                    .id(user.getDoctor().getId())
                    .uin(user.getDoctor().getUin())
                    .name(user.getDoctor().getName())
                    .isGp(user.getDoctor().isGp())
                    .build();
        }

        PatientDto.Summary patientSummary = null;
        if (user.getPatient() != null) {
            patientSummary = PatientDto.Summary.builder()
                    .id(user.getPatient().getId())
                    .name(user.getPatient().getName())
                    .egn(user.getPatient().getEgn())
                    .hasValidInsurance(user.getPatient().hasValidInsurance())
                    .build();
        }

        return UserDto.Response.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .doctor(doctorSummary)
                .patient(patientSummary)
                .build();
    }
}
