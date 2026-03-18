package com.fraudgraph.repository;

import com.fraudgraph.model.FraudConnection;
import com.fraudgraph.enums.ConnectionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FraudConnectionRepository extends JpaRepository<FraudConnection, Long> {

    List<FraudConnection> findByEntityValueAndConnectionType(String entityValue,
                                                             ConnectionType connectionType);

    List<FraudConnection> findByEntityValue(String entityValue);

    @Query("SELECT fc.entityValue, COUNT(fc) as cnt FROM FraudConnection fc " +
            "WHERE fc.connectionType = :type " +
            "GROUP BY fc.entityValue ORDER BY cnt DESC")
    List<Object[]> findMostFrequentEntitiesByType(ConnectionType type);
}