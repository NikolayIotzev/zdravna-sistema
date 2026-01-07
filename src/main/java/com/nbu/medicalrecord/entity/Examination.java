package com.nbu.medicalrecord.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "examinations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Examination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate examinationDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(optional = false)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "diagnosis_id")
    private Diagnosis diagnosis;

    @Column(length = 2000)
    private String treatment; // Prescribed treatment (лечение)

    @Column(length = 1000)
    private String prescription; // Prescription details (рецепта)

    @OneToOne(mappedBy = "examination", cascade = CascadeType.ALL)
    private SickLeave sickLeave;
}
