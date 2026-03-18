package com.fraudgraph.repository;

import com.fraudgraph.model.VerdictDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VerdictDetailRepository extends JpaRepository<VerdictDetail, Long> {
    List<VerdictDetail> findByClaimId(Long claimId);
}