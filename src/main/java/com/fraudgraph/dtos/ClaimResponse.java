package com.fraudgraph.dtos;

import com.fraudgraph.enums.ClaimStatus;
import com.fraudgraph.enums.RiskLevel;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ClaimResponse {
    private Long id;
    private String claimReference;
    private String claimantName;
    private String witnessName;
    private String garageName;
    private String lawyerName;
    private String doctorName;
    private String incidentLocation;
    private LocalDate incidentDate;
    private Double claimAmount;
    private String incidentDescription;
    private RiskLevel riskLevel;
    private ClaimStatus status;
    private LocalDateTime submittedAt;
    private boolean isDemoSeed;
    private String verdictSummary;
    private List<VerdictDetailDto> verdictDetails;
    private List<String> connectedClaimReferences;

}