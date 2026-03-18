package com.fraudgraph.repository;

import com.fraudgraph.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findAllByOrderBySubmittedAtDesc();
    List<Claim> findByWitnessName(String witnessName);
    List<Claim> findByGarageName(String garageName);
    List<Claim> findByLawyerName(String lawyerName);
    List<Claim> findByDoctorName(String doctorName);
    List<Claim> findByClaimantName(String claimantName);

    Optional<Claim> findByClaimReference(String ref);
}
