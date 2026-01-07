package com.nbu.medicalrecord.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

public class DoctorDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "УИН е задължителен")
        @Size(min = 6, max = 20, message = "УИН трябва да бъде между 6 и 20 символа")
        private String uin;

        @NotBlank(message = "Името е задължително")
        @Size(min = 2, max = 100, message = "Името трябва да бъде между 2 и 100 символа")
        private String name;

        private boolean isGp;

        private Set<Long> specialtyIds;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String uin;
        private String name;
        private boolean isGp;
        private Set<SpecialtyDto.Response> specialties;
        private int patientCount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Summary {
        private Long id;
        private String uin;
        private String name;
        private boolean isGp;
    }
}
