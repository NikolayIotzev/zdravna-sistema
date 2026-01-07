package com.nbu.medicalrecord.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

public class ExaminationDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        @NotNull(message = "Датата на прегледа е задължителна")
        @PastOrPresent(message = "Датата на прегледа не може да бъде в бъдещето")
        private LocalDate examinationDate;

        @NotNull(message = "Пациентът е задължителен")
        private Long patientId;

        private Long doctorId;

        private Long diagnosisId;

        @Size(max = 2000, message = "Лечението не може да надвишава 2000 символа")
        private String treatment;

        @Size(max = 1000, message = "Рецептата не може да надвишава 1000 символа")
        private String prescription;

        private SickLeaveDto.Request sickLeave;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private LocalDate examinationDate;
        private PatientDto.Summary patient;
        private DoctorDto.Summary doctor;
        private DiagnosisDto.Response diagnosis;
        private String treatment;
        private String prescription;
        private SickLeaveDto.Response sickLeave;
    }
}
