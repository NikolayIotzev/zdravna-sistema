package com.nbu.medicalrecord.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uin;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean isGp;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "doctor_specialties", joinColumns = @JoinColumn(name = "doctor_id"), inverseJoinColumns = @JoinColumn(name = "specialty_id"))
    @Builder.Default
    private Set<Specialty> specialties = new HashSet<>();

    @OneToMany(mappedBy = "gp")
    @Builder.Default
    private Set<Patient> patients = new HashSet<>(); // Patients registered with this GP

    @OneToMany(mappedBy = "doctor")
    @Builder.Default
    private Set<Examination> examinations = new HashSet<>();
}
