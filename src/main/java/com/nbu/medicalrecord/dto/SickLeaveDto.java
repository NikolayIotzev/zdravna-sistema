package com.nbu.medicalrecord.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

public class SickLeaveDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotNull(message = "Началната дата е задължителна")
        @FutureOrPresent(message = "Началната дата трябва да бъде днес или в бъдещето")
        private LocalDate startDate;

        @Min(value = 1, message = "Броят дни трябва да бъде поне 1")
        private int numberOfDays;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private LocalDate startDate;
        private int numberOfDays;
        private LocalDate endDate;
    }
}
