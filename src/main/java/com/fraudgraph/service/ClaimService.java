package com.fraudgraph.service;

import com.fraudgraph.dtos.ClaimResponse;
import com.fraudgraph.dtos.ClaimSubmitRequest;
import com.fraudgraph.dtos.GraphDataResponse;
import com.fraudgraph.dtos.VerdictDetailDto;
import com.fraudgraph.model.Claim;
import com.fraudgraph.model.FraudConnection;
import com.fraudgraph.enums.ClaimStatus;
import com.fraudgraph.enums.RiskLevel;
import com.fraudgraph.repository.ClaimRepository;
import com.fraudgraph.repository.FraudConnectionRepository;
import com.fraudgraph.repository.VerdictDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final FraudConnectionRepository fraudConnectionRepository;
    private final VerdictDetailRepository verdictDetailRepository;
    private final FraudAnalysisService fraudAnalysisService;

    public List<ClaimResponse> getAllClaims() {
        return claimRepository.findAllByOrderBySubmittedAtDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<ClaimResponse> getClaimById(Long id) {
        return claimRepository.findById(id).map(this::toResponse);
    }

    @Transactional
    public ClaimResponse submitClaim(ClaimSubmitRequest request) {
        // Build claim from request
        Claim claim = Claim.builder()
                .claimReference("CLM-" + String.format("%03d",
                        claimRepository.count() + 1))
                .claimantName(request.getClaimantName())
                .witnessName(request.getWitnessName())
                .garageName(request.getGarageName())
                .lawyerName(request.getLawyerName())
                .doctorName(request.getDoctorName())
                .incidentLocation(request.getIncidentLocation())
                .incidentDate(request.getIncidentDate())
                .claimAmount(request.getClaimAmount())
                .incidentDescription(request.getIncidentDescription())
                .submittedAt(LocalDateTime.now())
                .status(ClaimStatus.PENDING)
                .isDemoSeed(false)
                .build();

        // Save first to get ID (needed for FK relationships)
        Claim saved = claimRepository.save(claim);

        // Run fraud analysis
        FraudAnalysisService.AnalysisResult result =
                fraudAnalysisService.analyseClaim(saved);

        // Apply results
        saved.setRiskLevel(result.riskLevel);
        saved.setStatus(autoStatus(result.riskLevel));

        // Set claim reference on connections and details then save
        result.connections.forEach(fc -> fc.setSourceClaim(saved));
        result.verdictDetails.forEach(vd -> vd.setClaim(saved));

        fraudConnectionRepository.saveAll(result.connections);
        verdictDetailRepository.saveAll(result.verdictDetails);

        claimRepository.save(saved);

        return toResponse(saved);
    }

    @Transactional
    public ClaimResponse updateStatus(Long id, ClaimStatus newStatus) {
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Claim not found: " + id));
        claim.setStatus(newStatus);
        return toResponse(claimRepository.save(claim));
    }

    public GraphDataResponse getGraphData() {
        GraphDataResponse graph = new GraphDataResponse();

        claimRepository.findAll().forEach(claim -> {
            String riskStr = claim.getRiskLevel() != null
                    ? claim.getRiskLevel().name() : "LOW";

            graph.addNode(claim.getClaimReference(), "claim",
                    claim.getClaimantName() + "\n" + claim.getClaimReference(),
                    riskStr);

            fraudConnectionRepository
                    .findByEntityValue(claim.getWitnessName() != null
                            ? claim.getWitnessName() : "")
                    .forEach(fc -> {
                        // handled below via direct connection query
                    });
        });

        // Build graph from FraudConnections
        fraudConnectionRepository.findAll().forEach(fc -> {
            String entityNodeId = fc.getConnectionType().name().charAt(0)
                    + ":" + fc.getEntityValue();
            String typeLabel = fc.getConnectionType().name().toLowerCase();

            graph.addNode(entityNodeId, typeLabel, fc.getEntityValue(), null);
            graph.addEdge(fc.getSourceClaim().getClaimReference(),
                    entityNodeId, typeLabel);
        });

        return graph;
    }

    // ── Mapping ──

    private ClaimResponse toResponse(Claim claim) {
        List<VerdictDetailDto> detailDtos =
                verdictDetailRepository.findByClaimId(claim.getId())
                        .stream()
                        .map(vd -> VerdictDetailDto.builder()
                                .entityType(vd.getEntityType().name())
                                .entityValue(vd.getEntityValue())
                                .occurrenceCount(vd.getOccurrenceCount())
                                .explanation(vd.getExplanation())
                                .build())
                        .collect(Collectors.toList());

        List<String> connectedRefs =
                fraudConnectionRepository
                        .findByEntityValue(claim.getWitnessName() != null
                                ? claim.getWitnessName() : "")
                        .stream()
                        .map(FraudConnection::getConnectedClaimReference)
                        .distinct()
                        .collect(Collectors.toList());

        // Build verdict summary from details if not set
        String verdictSummary = buildVerdictFromDetails(
                claim, detailDtos);

        return ClaimResponse.builder()
                .id(claim.getId())
                .claimReference(claim.getClaimReference())
                .claimantName(claim.getClaimantName())
                .witnessName(claim.getWitnessName())
                .garageName(claim.getGarageName())
                .lawyerName(claim.getLawyerName())
                .doctorName(claim.getDoctorName())
                .incidentLocation(claim.getIncidentLocation())
                .incidentDate(claim.getIncidentDate())
                .claimAmount(claim.getClaimAmount())
                .incidentDescription(claim.getIncidentDescription())
                .riskLevel(claim.getRiskLevel())
                .status(claim.getStatus())
                .submittedAt(claim.getSubmittedAt())
                .isDemoSeed(claim.isDemoSeed())
                .verdictSummary(verdictSummary)
                .verdictDetails(detailDtos)
                .connectedClaimReferences(connectedRefs)
                .build();
    }

    private String buildVerdictFromDetails(
            Claim claim,
            List<VerdictDetailDto> details) {

        if (details.isEmpty()) {
            return "No shared entities detected. Claim is consistent " +
                    "with a genuine isolated incident.";
        }
        StringBuilder sb = new StringBuilder();
        details.forEach(d -> sb.append("• ").append(d.getExplanation()).append("\n"));
        return sb.toString().trim();
    }

    private ClaimStatus autoStatus(RiskLevel risk) {
        return switch (risk) {
            case LOW -> ClaimStatus.APPROVED;
            case MEDIUM -> ClaimStatus.INVESTIGATING;
            case HIGH -> ClaimStatus.ESCALATED;
        };
    }
}