package com.nbu.medicalrecord.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, length = 10)
    private String egn; // ЕГН - Bulgarian Personal Identification Number

    @Column
    private LocalDate lastInsurancePayment; // Last health insurance payment date

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "gp_id")
    private Doctor gp; // General Practitioner (личен лекар)

    @OneToMany(mappedBy = "patient")
    @Builder.Default
    private Set<Examination> examinations = new HashSet<>();

    /**
     * Checks if health insurance is paid for the last 6 months
     */
    public boolean hasValidInsurance() {
        if (lastInsurancePayment == null) {
            return false;
        }
        return lastInsurancePayment.isAfter(LocalDate.now().minusMonths(6));
    }
}
