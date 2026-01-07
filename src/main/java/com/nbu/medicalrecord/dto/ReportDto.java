package com.nbu.medicalrecord.dto;

import lombok.*;

import java.util.List;

public class ReportDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DoctorPatientCount {
        private DoctorDto.Summary doctor;
        private long patientCount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DoctorExaminationCount {
        private DoctorDto.Summary doctor;
        private long examinationCount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DiagnosisFrequency {
        private DiagnosisDto.Response diagnosis;
        private long frequency;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthSickLeaveCount {
        private int month;
        private int year;
        private long count;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DoctorSickLeaveCount {
        private DoctorDto.Summary doctor;
        private long sickLeaveCount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PatientExaminations {
        private PatientDto.Summary patient;
        private List<ExaminationDto.Response> examinations;
    }
}
