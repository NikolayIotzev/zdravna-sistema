package com.nbu.medicalrecord.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

public class PatientDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "Името е задължително")
        @Size(min = 2, max = 100, message = "Името трябва да бъде между 2 и 100 символа")
        private String name;

        @NotBlank(message = "ЕГН е задължително")
        @Pattern(regexp = "^\\d{10}$", message = "ЕГН трябва да съдържа точно 10 цифри")
        private String egn;

        private LocalDate lastInsurancePayment;

        private Long gpId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String name;
        private String egn;
        private LocalDate lastInsurancePayment;
        private boolean hasValidInsurance;
        private DoctorDto.Summary gp;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Summary {
        private Long id;
        private String name;
        private String egn;
        private boolean hasValidInsurance;
    }
}
