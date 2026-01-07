package com.nbu.medicalrecord.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "diagnoses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Diagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // Diagnosis code (e.g., ICD-10)

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @OneToMany(mappedBy = "diagnosis")
    @Builder.Default
    private Set<Examination> examinations = new HashSet<>();
}
