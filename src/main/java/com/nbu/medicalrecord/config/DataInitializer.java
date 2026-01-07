package com.nbu.medicalrecord.config;

import com.nbu.medicalrecord.entity.*;
import com.nbu.medicalrecord.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final SpecialtyRepository specialtyRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() == 0) {
            initializeData();
            log.info("Initial data has been loaded successfully");
        }
    }

    private void initializeData() {
        // Create specialties
        Specialty generalMedicine = createSpecialty("Обща медицина");
        Specialty cardiology = createSpecialty("Кардиология");
        Specialty neurology = createSpecialty("Неврология");
        Specialty pediatrics = createSpecialty("Педиатрия");
        Specialty orthopedics = createSpecialty("Ортопедия");

        // Create diagnoses
        createDiagnosis("J06.9", "Остра инфекция на горните дихателни пътища", "Остра инфекция на горните дихателни пътища, неуточнена");
        createDiagnosis("J20.9", "Остър бронхит", "Остър бронхит, неуточнен");
        createDiagnosis("I10", "Есенциална (първична) хипертония", "Повишено кръвно налягане");
        createDiagnosis("K29.7", "Гастрит", "Гастрит, неуточнен");
        createDiagnosis("M54.5", "Болка в кръста", "Лумбаго, неуточнено");

        // Create admin user
        User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .role(User.Role.ADMIN)
                .build();
        userRepository.save(admin);

        // Create doctor user with GP role
        User doctorUser1 = User.builder()
                .username("doctor1")
                .password(passwordEncoder.encode("doctor123"))
                .role(User.Role.DOCTOR)
                .build();
        doctorUser1 = userRepository.save(doctorUser1);

        Doctor doctor1 = Doctor.builder()
                .uin("1234567890")
                .name("Д-р Иван Петров")
                .isGp(true)
                .specialties(Set.of(generalMedicine))
                .user(doctorUser1)
                .build();
        doctor1 = doctorRepository.save(doctor1);

        // Create another doctor (specialist)
        User doctorUser2 = User.builder()
                .username("doctor2")
                .password(passwordEncoder.encode("doctor123"))
                .role(User.Role.DOCTOR)
                .build();
        doctorUser2 = userRepository.save(doctorUser2);

        Doctor doctor2 = Doctor.builder()
                .uin("0987654321")
                .name("Д-р Мария Георгиева")
                .isGp(false)
                .specialties(Set.of(cardiology, neurology))
                .user(doctorUser2)
                .build();
        doctorRepository.save(doctor2);

        // Create patient user
        User patientUser = User.builder()
                .username("patient1")
                .password(passwordEncoder.encode("patient123"))
                .role(User.Role.PATIENT)
                .build();
        patientUser = userRepository.save(patientUser);

        Patient patient1 = Patient.builder()
                .name("Георги Димитров")
                .egn("8501011234")
                .lastInsurancePayment(LocalDate.now().minusMonths(2))
                .gp(doctor1)
                .user(patientUser)
                .build();
        patientRepository.save(patient1);

        // Create another patient with user account
        User patientUser2 = User.builder()
                .username("patient2")
                .password(passwordEncoder.encode("patient123"))
                .role(User.Role.PATIENT)
                .build();
        patientUser2 = userRepository.save(patientUser2);

        Patient patient2 = Patient.builder()
                .name("Мария Иванова")
                .egn("9002025678")
                .lastInsurancePayment(LocalDate.now().minusMonths(8)) // Invalid insurance
                .gp(doctor1)
                .user(patientUser2)
                .build();
        patientRepository.save(patient2);

        log.info("Created admin user: admin/admin123");
        log.info("Created doctor user: doctor1/doctor123 (GP)");
        log.info("Created doctor user: doctor2/doctor123 (Specialist)");
        log.info("Created patient user: patient1/patient123");
        log.info("Created patient user: patient2/patient123");
    }

    private Specialty createSpecialty(String name) {
        Specialty specialty = Specialty.builder()
                .name(name)
                .build();
        return specialtyRepository.save(specialty);
    }

    private void createDiagnosis(String code, String name, String description) {
        Diagnosis diagnosis = Diagnosis.builder()
                .code(code)
                .name(name)
                .description(description)
                .build();
        diagnosisRepository.save(diagnosis);
    }
}
