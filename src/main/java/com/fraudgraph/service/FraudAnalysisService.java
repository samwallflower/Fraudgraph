package com.fraudgraph.service;

import com.fraudgraph.model.Claim;
import com.fraudgraph.model.FraudConnection;
import com.fraudgraph.enums.ConnectionType;
import com.fraudgraph.model.VerdictDetail;
import com.fraudgraph.enums.RiskLevel;
import com.fraudgraph.repository.ClaimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FraudAnalysisService {

    private final ClaimRepository claimRepository;

    public static class AnalysisResult {
        public RiskLevel riskLevel;
        public String verdictSummary;
        public List<FraudConnection> connections;
        public List<VerdictDetail> verdictDetails;

        public AnalysisResult(RiskLevel riskLevel, String verdictSummary,
                              List<FraudConnection> connections,
                              List<VerdictDetail> verdictDetails) {
            this.riskLevel = riskLevel;
            this.verdictSummary = verdictSummary;
            this.connections = connections;
            this.verdictDetails = verdictDetails;
        }
    }

    private static class EntityCheck {
        ConnectionType type;
        String value;
        List<Claim> matches;

        EntityCheck(ConnectionType type, String value, List<Claim> matches) {
            this.type = type;
            this.value = value;
            this.matches = matches;
        }
    }

    public AnalysisResult analyseClaim(Claim newClaim) {
        List<EntityCheck> checks = new ArrayList<>();

        if (hasValue(newClaim.getWitnessName()))
            checks.add(new EntityCheck(ConnectionType.WITNESS, newClaim.getWitnessName(),
                    claimRepository.findByWitnessName(newClaim.getWitnessName())
                            .stream()
                            .filter(c -> !c.getId().equals(newClaim.getId()))
                            .collect(Collectors.toList())));

        if (hasValue(newClaim.getGarageName()))
            checks.add(new EntityCheck(ConnectionType.GARAGE, newClaim.getGarageName(),
                    claimRepository.findByGarageName(newClaim.getGarageName())
                            .stream()
                            .filter(c -> !c.getId().equals(newClaim.getId()))
                            .collect(Collectors.toList())));

        if (hasValue(newClaim.getLawyerName()))
            checks.add(new EntityCheck(ConnectionType.LAWYER, newClaim.getLawyerName(),
                    claimRepository.findByLawyerName(newClaim.getLawyerName())
                            .stream()
                            .filter(c -> !c.getId().equals(newClaim.getId()))
                            .collect(Collectors.toList())));

        if (hasValue(newClaim.getDoctorName()))
            checks.add(new EntityCheck(ConnectionType.DOCTOR, newClaim.getDoctorName(),
                    claimRepository.findByDoctorName(newClaim.getDoctorName())
                            .stream()
                            .filter(c -> !c.getId().equals(newClaim.getId()))
                            .collect(Collectors.toList())));


        // Filter to only checks that actually found matches
        List<EntityCheck> hits = checks.stream()
                .filter(c -> !c.matches.isEmpty())
                .collect(Collectors.toList());

        // Build FraudConnection objects
        List<FraudConnection> connections = new ArrayList<>();
        for (EntityCheck hit : hits) {
            for (Claim matched : hit.matches) {
                connections.add(FraudConnection.builder()
                        .sourceClaim(newClaim)
                        .connectedClaimReference(matched.getClaimReference())
                        .connectionType(hit.type)
                        .entityValue(hit.value)
                        .build());
            }
        }

        // Build VerdictDetail objects
        List<VerdictDetail> verdictDetails = new ArrayList<>();
        for (EntityCheck hit : hits) {
            String explanation = String.format(
                    "%s '%s' appears in %d previous claim(s): %s",
                    hit.type.name(), hit.value, hit.matches.size(),
                    hit.matches.stream().map(Claim::getClaimReference)
                            .collect(Collectors.joining(", "))
            );
            verdictDetails.add(VerdictDetail.builder()
                    .claim(newClaim)
                    .entityType(hit.type)
                    .entityValue(hit.value)
                    .occurrenceCount(hit.matches.size())
                    .explanation(explanation)
                    .build());
        }

        // Determine risk level
        int hitCount = hits.size();
        int maxOccurrences = hits.stream()
                .mapToInt(h -> h.matches.size())
                .max().orElse(0);

        Set<String> uniqueConnectedRefs = connections.stream()
                .map(FraudConnection::getConnectedClaimReference)
                .collect(Collectors.toSet());

        RiskLevel riskLevel;
        if (hitCount == 0) {
            riskLevel = RiskLevel.LOW;
        } else if (hitCount >= 3 || maxOccurrences >= 4 || uniqueConnectedRefs.size() >= 5) {
            riskLevel = RiskLevel.HIGH;
        } else {
            riskLevel = RiskLevel.MEDIUM;
        }

        String verdictSummary = buildVerdictSummary(
                newClaim, riskLevel, hits, uniqueConnectedRefs);

        return new AnalysisResult(riskLevel, verdictSummary, connections, verdictDetails);
    }

    private String buildVerdictSummary(Claim claim, RiskLevel level,
                                       List<EntityCheck> hits,
                                       Set<String> connectedRefs) {
        return switch (level) {
            case LOW -> String.format(
                    "AI Analysis — LOW RISK\n\n" +
                            "No shared entities detected. Claimant '%s' and all named parties " +
                            "are unique to this submission. Consistent with a genuine isolated incident.\n\n" +
                            "Recommendation: Approve for standard processing.",
                    claim.getClaimantName());

            case MEDIUM -> {
                StringBuilder sb = new StringBuilder();
                sb.append("AI Analysis — MEDIUM RISK\n\n");
                sb.append(String.format(
                        "Partial network connections detected for '%s'.\n\n",
                        claim.getClaimantName()));
                sb.append("Flagged entities:\n");
                hits.forEach(h -> sb.append(String.format(
                        "• %s '%s' appears in %d previous claim(s)\n",
                        h.type, h.value, h.matches.size())));
                sb.append("\nRecommendation: Route to investigator. Do not auto-approve.");
                yield sb.toString();
            }

            case HIGH -> {
                StringBuilder sb = new StringBuilder();
                sb.append("AI Analysis — HIGH RISK — FRAUD RING DETECTED\n\n");
                sb.append(String.format(
                        "Claim by '%s' is connected to a known fraud network.\n\n",
                        claim.getClaimantName()));
                sb.append("Critical findings:\n");
                hits.forEach(h -> sb.append(String.format(
                        "• %s '%s' appears across %d separate claims — " +
                                "statistically impossible in genuine incidents\n",
                        h.type, h.value, h.matches.size())));
                sb.append(String.format(
                        "\nTotal network exposure: %d connected claims.\n\n",
                        connectedRefs.size()));
                sb.append("Recommendation: FREEZE claim. Escalate full network " +
                        "to fraud investigation team immediately.");
                yield sb.toString();
            }
        };
    }

    private boolean hasValue(String s) {
        return s != null && !s.isBlank();
    }
}