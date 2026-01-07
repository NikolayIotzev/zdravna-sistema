package com.nbu.medicalrecord.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

public class DiagnosisDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotBlank(message = "Кодът на диагнозата е задължителен")
        @Size(min = 1, max = 20, message = "Кодът трябва да бъде между 1 и 20 символа")
        private String code;

        @NotBlank(message = "Името на диагнозата е задължително")
        @Size(min = 2, max = 200, message = "Името трябва да бъде между 2 и 200 символа")
        private String name;

        @Size(max = 1000, message = "Описанието не може да надвишава 1000 символа")
        private String description;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String code;
        private String name;
        private String description;
    }
}
