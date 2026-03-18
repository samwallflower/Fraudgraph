package com.fraudgraph.model;

import com.fraudgraph.enums.ConnectionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "verdict_details")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerdictDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    private Claim claim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConnectionType entityType;

    @Column(nullable = false)
    private String entityValue;

    private int occurrenceCount;

    // Plain-language explanation for this specific entity match
    @Column(columnDefinition = "TEXT")
    private String explanation;
}