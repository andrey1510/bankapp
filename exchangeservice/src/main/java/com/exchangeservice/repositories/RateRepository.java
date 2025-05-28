package com.exchangeservice.repositories;

import com.exchangeservice.entities.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RateRepository extends JpaRepository<Rate, UUID> {

    @Query(value = """
        SELECT DISTINCT ON (name) *
        FROM rates
        ORDER BY name, timestamp DESC
        """, nativeQuery = true)
    List<Rate> findLatestRates();

    @Query("SELECT r FROM Rate r WHERE r.name = :name ORDER BY r.timestamp DESC LIMIT 1")
    Optional<Rate> findLatestRateByName(String name);
}
