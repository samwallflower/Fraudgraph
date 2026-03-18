package com.fraudgraph.controller;

import com.fraudgraph.dtos.ClaimResponse;
import com.fraudgraph.dtos.ClaimSubmitRequest;
import com.fraudgraph.dtos.GraphDataResponse;
import com.fraudgraph.enums.ClaimStatus;
import com.fraudgraph.service.ClaimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/claims")
@CrossOrigin(origins = "*")
public class ClaimController {

    @Autowired
    private ClaimService claimService;

    @GetMapping("/all")
    public ResponseEntity<List<ClaimResponse>> getAllClaims() {
        return ResponseEntity.ok(claimService.getAllClaims());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClaimResponse> getClaimById(@PathVariable Long id) {
        return claimService.getClaimById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/submit")
    public ResponseEntity<ClaimResponse> submitClaim(
            @RequestBody ClaimSubmitRequest request) {
        return ResponseEntity.ok(claimService.submitClaim(request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ClaimResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        ClaimStatus newStatus = ClaimStatus.valueOf(body.get("status"));
        return ResponseEntity.ok(claimService.updateStatus(id, newStatus));
    }

    @GetMapping("/graph")
    public ResponseEntity<GraphDataResponse> getGraphData() {
        return ResponseEntity.ok(claimService.getGraphData());
    }
}