package com.fraudgraph.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ClaimSubmitRequest {
    private String claimantName;
    private String witnessName;
    private String garageName;
    private String lawyerName;
    private String doctorName;
    private String incidentLocation;
    private LocalDate incidentDate;
    private Double claimAmount;
    private String incidentDescription;
}
