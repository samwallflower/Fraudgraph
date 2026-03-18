package com.fraudgraph.data;

import com.fraudgraph.model.Claim;
import com.fraudgraph.model.FraudConnection;
import com.fraudgraph.enums.ConnectionType;
import com.fraudgraph.model.VerdictDetail;
import com.fraudgraph.enums.ClaimStatus;
import com.fraudgraph.enums.RiskLevel;
import com.fraudgraph.repository.ClaimRepository;
import com.fraudgraph.repository.FraudConnectionRepository;
import com.fraudgraph.repository.VerdictDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DemoDataSeeder implements CommandLineRunner {

    private final ClaimRepository claimRepository;
    private final FraudConnectionRepository fraudConnectionRepository;
    private final VerdictDetailRepository verdictDetailRepository;

    // Fraud ring entity values
    private static final String RING_WITNESS = "Kovács János";
    private static final String RING_GARAGE  = "AutoFix Budapest Kft";
    private static final String RING_LAWYER  = "Dr. Szabó Law Office";

    @Override
    public void run(String... args) {
        //seedCleanClaims();
        //seedFraudRing();
        //seedMediumRiskClaims();
        //seedFraudRingConnections();
    }

    private void seedCleanClaims() {
        List.of(
                buildClaim("CLM-001","Horváth Béla","Fekete Sándor","Budapest AutoCare","Dr. Varga Law","Dr. Kiss Imre","Budapest, Üllői út",LocalDate.of(2024,1,8),1850.0,"Rear-end collision. Minor bumper damage.",RiskLevel.LOW,ClaimStatus.APPROVED),
                buildClaim("CLM-002","Szabó Margit","Tóth Erzsébet","Pécs Motor Works","Kovács & Associates","Dr. Balogh Mária","Pécs, Rákóczi út",LocalDate.of(2024,1,15),3200.0,"Side impact at junction. Door damage.",RiskLevel.LOW,ClaimStatus.APPROVED),
                buildClaim("CLM-003","Papp György","Molnár Judit","Debrecen Garage","Farkas Legal","Dr. Simon Péter","Debrecen, Piac utca",LocalDate.of(2024,1,22),2750.0,"Parking lot collision. Front bumper damage.",RiskLevel.LOW,ClaimStatus.APPROVED),
                buildClaim("CLM-004","Németh Katalin","Bíró István","Győr AutoFix","Mészáros Law","Dr. Takács Zoltán","Győr, Árpád út",LocalDate.of(2024,2,3),4100.0,"Highway merge accident.",RiskLevel.LOW,ClaimStatus.APPROVED),
                buildClaim("CLM-005","Varga Tibor","Lukács Éva","Miskolc Car Service","Hegedűs & Partners","Dr. Kovács Anna","Miskolc, Bajcsy út",LocalDate.of(2024,2,14),1620.0,"Low speed rear collision.",RiskLevel.LOW,ClaimStatus.APPROVED),
                buildClaim("CLM-006","Kiss Zoltán","Hajdú Péter","Eger Panel Workshop","Szabó Legal Group","Dr. Fehér László","Eger, Kossuth utca",LocalDate.of(2024,2,28),2980.0,"T-bone collision at crossroads.",RiskLevel.LOW,ClaimStatus.APPROVED),
                buildClaim("CLM-007","Fodor Miklós","Cseke Réka","Kaposvár Motors","Németh Law","Dr. Horváth Tibor","Kaposvár, Rippl-Rónai utca",LocalDate.of(2024,3,6),3450.0,"Rear-end motorway incident.",RiskLevel.LOW,ClaimStatus.APPROVED),
                buildClaim("CLM-008","Halász Éva","Szilágyi Bence","Nyíregyháza Auto","Barta & Sons","Dr. Molnár Katalin","Nyíregyháza, Dózsa György utca",LocalDate.of(2024,3,19),1900.0,"Minor collision reversing from parking.",RiskLevel.LOW,ClaimStatus.APPROVED),
                buildClaim("CLM-009","Balogh Róbert","Kardos Nóra","Szombathely Repairs","Fekete Law","Dr. Vass Gábor","Szombathely, Savaria tér",LocalDate.of(2024,4,2),5200.0,"Head-on low speed collision.",RiskLevel.LOW,ClaimStatus.APPROVED),
                buildClaim("CLM-010","Sipos András","Gerencsér Lilla","Zalaegerszeg Workshop","Papp Legal","Dr. Nagy Ildikó","Zalaegerszeg, Kovács tér",LocalDate.of(2024,4,17),2100.0,"Roundabout collision.",RiskLevel.LOW,ClaimStatus.APPROVED)
        ).forEach(claimRepository::save);
    }

    private void seedFraudRing() {
        List.of(
                buildClaim("CLM-011","Tóth László",   RING_WITNESS,RING_GARAGE,RING_LAWYER,"Dr. Rácz Béla","Budapest, Váci út",     LocalDate.of(2024,5,3), 3800.0,"Rear-end collision near Westend.",RiskLevel.HIGH,ClaimStatus.ESCALATED),
                buildClaim("CLM-012","Farkas Imre",   RING_WITNESS,RING_GARAGE,RING_LAWYER,"Dr. Rácz Béla","Budapest, Andrássy út", LocalDate.of(2024,5,11),4200.0,"Side collision at junction.",     RiskLevel.HIGH,ClaimStatus.ESCALATED),
                buildClaim("CLM-013","Mezei Péter",   RING_WITNESS,RING_GARAGE,RING_LAWYER,"Dr. Rácz Béla","Budapest, Rákóczi út",  LocalDate.of(2024,5,19),3600.0,"T-bone at zebra crossing.",       RiskLevel.HIGH,ClaimStatus.ESCALATED),
                buildClaim("CLM-014","Beke Norbert",  RING_WITNESS,RING_GARAGE,RING_LAWYER,"Dr. Rácz Béla","Budapest, Hungária krt",LocalDate.of(2024,6,2), 5100.0,"Rear collision on ring road.",    RiskLevel.HIGH,ClaimStatus.ESCALATED),
                buildClaim("CLM-015","Oláh Gábor",    RING_WITNESS,RING_GARAGE,RING_LAWYER,"Dr. Rácz Béla","Budapest, Üllői út",    LocalDate.of(2024,6,14),4750.0,"Front-end collision at lights.",  RiskLevel.HIGH,ClaimStatus.ESCALATED)
        ).forEach(claimRepository::save);
    }

    private void seedMediumRiskClaims() {
        List.of(
                buildClaim("CLM-016","Lengyel Dóra","Tamás Gergő",     RING_GARAGE,        "Soós & Partners","Dr. Orbán Csilla","Budapest, Bartók Béla út",LocalDate.of(2024,7,8), 2300.0,"Minor collision in car park.", RiskLevel.MEDIUM,ClaimStatus.INVESTIGATING),
                buildClaim("CLM-017","Pintér Csaba","Boros Henrietta","Kecskemét Panel",    RING_LAWYER,      "Dr. Juhász Ferenc","Kecskemét, Kéttemplom köz",LocalDate.of(2024,7,22),2800.0,"Side swipe on narrow road.", RiskLevel.MEDIUM,ClaimStatus.INVESTIGATING)
        ).forEach(claimRepository::save);
    }

    private void seedFraudRingConnections() {
        List<String> ringRefs = List.of(
                "CLM-011","CLM-012","CLM-013","CLM-014","CLM-015");

        ringRefs.forEach(ref -> {
            Claim source = claimRepository.findByClaimReference(ref)
                    .orElseThrow();

            // Connect to all other ring members
            ringRefs.stream()
                    .filter(r -> !r.equals(ref))
                    .forEach(otherRef -> {
                        fraudConnectionRepository.save(FraudConnection.builder()
                                .sourceClaim(source)
                                .connectedClaimReference(otherRef)
                                .connectionType(ConnectionType.WITNESS)
                                .entityValue(RING_WITNESS)
                                .build());
                        fraudConnectionRepository.save(FraudConnection.builder()
                                .sourceClaim(source)
                                .connectedClaimReference(otherRef)
                                .connectionType(ConnectionType.GARAGE)
                                .entityValue(RING_GARAGE)
                                .build());
                        fraudConnectionRepository.save(FraudConnection.builder()
                                .sourceClaim(source)
                                .connectedClaimReference(otherRef)
                                .connectionType(ConnectionType.LAWYER)
                                .entityValue(RING_LAWYER)
                                .build());
                    });

            // Verdict details for each ring claim
            verdictDetailRepository.save(VerdictDetail.builder()
                    .claim(source)
                    .entityType(ConnectionType.WITNESS)
                    .entityValue(RING_WITNESS)
                    .occurrenceCount(5)
                    .explanation("Witness '" + RING_WITNESS + "' appears across 5 separate claims — statistically impossible in genuine incidents.")
                    .build());
            verdictDetailRepository.save(VerdictDetail.builder()
                    .claim(source)
                    .entityType(ConnectionType.GARAGE)
                    .entityValue(RING_GARAGE)
                    .occurrenceCount(5)
                    .explanation("Garage '" + RING_GARAGE + "' cited in 5 separate claims by unrelated claimants.")
                    .build());
            verdictDetailRepository.save(VerdictDetail.builder()
                    .claim(source)
                    .entityType(ConnectionType.LAWYER)
                    .entityValue(RING_LAWYER)
                    .occurrenceCount(5)
                    .explanation("Lawyer '" + RING_LAWYER + "' represents claimants across 5 separate unrelated incidents.")
                    .build());
        });

        // Medium risk connections
        Claim clm016 = claimRepository.findByClaimReference("CLM-016").orElseThrow();
        ringRefs.forEach(ref ->
                fraudConnectionRepository.save(FraudConnection.builder()
                        .sourceClaim(clm016)
                        .connectedClaimReference(ref)
                        .connectionType(ConnectionType.GARAGE)
                        .entityValue(RING_GARAGE)
                        .build()));
        verdictDetailRepository.save(VerdictDetail.builder()
                .claim(clm016)
                .entityType(ConnectionType.GARAGE)
                .entityValue(RING_GARAGE)
                .occurrenceCount(5)
                .explanation("Garage '" + RING_GARAGE + "' appears in 5 previous claims including active fraud ring investigation.")
                .build());

        Claim clm017 = claimRepository.findByClaimReference("CLM-017").orElseThrow();
        ringRefs.forEach(ref ->
                fraudConnectionRepository.save(FraudConnection.builder()
                        .sourceClaim(clm017)
                        .connectedClaimReference(ref)
                        .connectionType(ConnectionType.LAWYER)
                        .entityValue(RING_LAWYER)
                        .build()));
        verdictDetailRepository.save(VerdictDetail.builder()
                .claim(clm017)
                .entityType(ConnectionType.LAWYER)
                .entityValue(RING_LAWYER)
                .occurrenceCount(5)
                .explanation("Lawyer '" + RING_LAWYER + "' appears in 5 previous claims including active fraud ring investigation.")
                .build());
    }

    private Claim buildClaim(String ref, String claimant, String witness,
                             String garage, String lawyer, String doctor,
                             String location, LocalDate date, Double amount,
                             String description, RiskLevel risk,
                             ClaimStatus status) {
        return claimRepository.save(Claim.builder()
                .claimReference(ref)
                .claimantName(claimant)
                .witnessName(witness)
                .garageName(garage)
                .lawyerName(lawyer)
                .doctorName(doctor)
                .incidentLocation(location)
                .incidentDate(date)
                .claimAmount(amount)
                .incidentDescription(description)
                .riskLevel(risk)
                .status(status)
                .submittedAt(LocalDateTime.now().minusDays((long)(Math.random() * 120)))
                .isDemoSeed(true)
                .build());
    }
}