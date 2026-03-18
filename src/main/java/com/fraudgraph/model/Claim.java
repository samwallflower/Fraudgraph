package com.fraudgraph.model;

import com.fraudgraph.enums.ClaimStatus;
import com.fraudgraph.enums.RiskLevel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "claims")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String claimReference;

    @Column(nullable = false)
    private String claimantName;

    private String witnessName;
    private String garageName;
    private String lawyerName;
    private String doctorName;
    private String incidentLocation;
    private LocalDate incidentDate;
    private Double claimAmount;

    @Column(columnDefinition = "TEXT")
    private String incidentDescription;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @Enumerated(EnumType.STRING)
    private ClaimStatus status;

    private LocalDateTime submittedAt;
    private boolean isDemoSeed;


    @OneToMany(mappedBy = "sourceClaim", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<FraudConnection> fraudConnections = new ArrayList<>();

    @OneToMany(mappedBy = "claim", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<VerdictDetail> verdictDetails = new ArrayList<>();
}