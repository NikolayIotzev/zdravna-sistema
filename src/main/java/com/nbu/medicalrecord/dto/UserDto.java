package com.nbu.medicalrecord.dto;

import com.nbu.medicalrecord.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

public class UserDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RegisterRequest {
        @NotBlank(message = "Потребителското име е задължително")
        @Size(min = 3, max = 50, message = "Потребителското име трябва да бъде между 3 и 50 символа")
        private String username;

        @NotBlank(message = "Паролата е задължителна")
        @Size(min = 6, max = 100, message = "Паролата трябва да бъде между 6 и 100 символа")
        private String password;

        @NotNull(message = "Ролята е задължителна")
        private User.Role role;

        // For doctor registration
        private DoctorDto.Request doctor;

        // For patient registration
        private PatientDto.Request patient;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginRequest {
        @NotBlank(message = "Потребителското име е задължително")
        private String username;

        @NotBlank(message = "Паролата е задължителна")
        private String password;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String username;
        private User.Role role;
        private DoctorDto.Summary doctor;
        private PatientDto.Summary patient;
    }
}
