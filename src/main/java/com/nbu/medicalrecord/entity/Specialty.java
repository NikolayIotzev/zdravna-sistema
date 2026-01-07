package com.nbu.medicalrecord.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "specialties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Specialty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "specialties")
    private Set<Doctor> doctors = new HashSet<>();
}
