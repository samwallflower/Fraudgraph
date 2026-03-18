package com.fraudgraph.model;

import com.fraudgraph.enums.ConnectionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fraud_connections")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_claim_id", nullable = false)
    private Claim sourceClaim;

    // The claim this connects to
    @Column(nullable = false)
    private String connectedClaimReference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConnectionType connectionType;

    @Column(nullable = false)
    private String entityValue;


}