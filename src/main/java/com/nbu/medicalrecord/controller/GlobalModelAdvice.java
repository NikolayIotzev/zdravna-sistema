package com.nbu.medicalrecord.controller;

import com.nbu.medicalrecord.dto.DoctorDto;
import com.nbu.medicalrecord.dto.PatientDto;
import com.nbu.medicalrecord.dto.UserDto;
import com.nbu.medicalrecord.service.DoctorService;
import com.nbu.medicalrecord.service.PatientService;
import com.nbu.medicalrecord.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAdvice {

    private final UserService userService;
    private final PatientService patientService;
    private final DoctorService doctorService;

    @ModelAttribute("currentUserDisplayName")
    public String currentUserDisplayName(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String username = authentication.getName();
        if ("anonymousUser".equals(username)) {
            return null;
        }

        try {
            UserDto.Response user = userService.getByUsername(username);

            // Check if user is a patient
            PatientDto.Response patient = patientService.getByUserId(user.getId());
            if (patient != null) {
                return patient.getName();
            }

            // Check if user is a doctor
            DoctorDto.Response doctor = doctorService.getByUserId(user.getId());
            if (doctor != null) {
                return doctor.getName();
            }

            // Fallback to username
            return username;
        } catch (Exception e) {
            return username;
        }
    }

    @ModelAttribute("currentUsername")
    public String currentUsername(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String username = authentication.getName();
        return "anonymousUser".equals(username) ? null : username;
    }
}
