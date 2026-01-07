package com.nbu.medicalrecord.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "sick_leaves")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SickLeave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private int numberOfDays;

    @OneToOne
    @JoinColumn(name = "examination_id", nullable = false)
    private Examination examination;

    public LocalDate getEndDate() {
        return startDate.plusDays(numberOfDays - 1);
    }
}
