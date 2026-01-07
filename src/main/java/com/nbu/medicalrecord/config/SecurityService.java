package com.nbu.medicalrecord.config;

import com.nbu.medicalrecord.entity.Examination;
import com.nbu.medicalrecord.entity.Patient;
import com.nbu.medicalrecord.entity.User;
import com.nbu.medicalrecord.repository.ExaminationRepository;
import com.nbu.medicalrecord.repository.PatientRepository;
import com.nbu.medicalrecord.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("securityService")
@RequiredArgsConstructor
public class SecurityService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final ExaminationRepository examinationRepository;

    /**
     * Check if the authenticated user is the owner of the patient record
     */
    public boolean isOwnerPatient(Long patientId, Authentication authentication) {
        if (authentication == null) {
            return false;
        }

        Optional<User> userOpt = userRepository.findByUsername(authentication.getName());
        if (userOpt.isEmpty() || userOpt.get().getPatient() == null) {
            return false;
        }

        return userOpt.get().getPatient().getId().equals(patientId);
    }

    /**
     * Check if the authenticated user can access the examination
     * (either as the patient who had the examination or as a doctor)
     */
    public boolean canAccessExamination(Long examinationId, Authentication authentication) {
        if (authentication == null) {
            return false;
        }

        Optional<User> userOpt = userRepository.findByUsername(authentication.getName());
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        Optional<Examination> examOpt = examinationRepository.findById(examinationId);
        if (examOpt.isEmpty()) {
            return false;
        }

        Examination examination = examOpt.get();

        // Patient can view their own examinations
        if (user.getPatient() != null && user.getPatient().getId().equals(examination.getPatient().getId())) {
            return true;
        }

        // Doctor can view examinations they performed
        if (user.getDoctor() != null && user.getDoctor().getId().equals(examination.getDoctor().getId())) {
            return true;
        }

        return false;
    }

    /**
     * Check if the authenticated user is the doctor who performed the examination
     */
    public boolean isDoctorForExamination(Long examinationId, Authentication authentication) {
        if (authentication == null) {
            return false;
        }

        Optional<User> userOpt = userRepository.findByUsername(authentication.getName());
        if (userOpt.isEmpty() || userOpt.get().getDoctor() == null) {
            return false;
        }

        Optional<Examination> examOpt = examinationRepository.findById(examinationId);
        if (examOpt.isEmpty()) {
            return false;
        }

        return userOpt.get().getDoctor().getId().equals(examOpt.get().getDoctor().getId());
    }

    /**
     * Check if the authenticated user is the doctor with the given ID
     * Used to validate that doctors can only create examinations for themselves
     */
    public boolean isDoctorWithId(Long doctorId, Authentication authentication) {
        if (authentication == null) {
            return false;
        }

        Optional<User> userOpt = userRepository.findByUsername(authentication.getName());
        if (userOpt.isEmpty() || userOpt.get().getDoctor() == null) {
            return false;
        }

        return userOpt.get().getDoctor().getId().equals(doctorId);
    }
}
