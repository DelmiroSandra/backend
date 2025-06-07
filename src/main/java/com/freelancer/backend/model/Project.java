package com.freelancer.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Column(nullable = false, length = 2000)
    private String description;

    // Orçamento estimado em alguma moeda (ex: valor em centavos)
    @Column(nullable = false)
    private Double budget;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    // Prazo em dias ou data específica
    private Instant deadline;

    // Status: "OPEN", "IN_PROGRESS", "COMPLETED", "CANCELLED"
    @Column(nullable = false)
    private String status = "OPEN";

    // Relação Many-to-One: contratante que criou o projeto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contractor_id", nullable = false)
    private User contractor;

    // Candidaturas recebidas
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Application> applications = new HashSet<>();
}
