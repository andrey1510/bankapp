package com.exchangeservice.repositories;

import com.exchangeservice.entities.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public interface RateRepository extends JpaRepository<Rate, UUID> {

    @Query(value = """
        SELECT DISTINCT ON (currency) *
        FROM rates
        ORDER BY currency, timestamp DESC
        """, nativeQuery = true)
    List<Rate> findLatestRates();

    @Query("SELECT r FROM Rate r WHERE r.currency = :currency ORDER BY r.timestamp DESC LIMIT 1")
    Optional<Rate> findLatestRateByCurrency(String currency);

    @Query("SELECT DISTINCT r.currency, r.title FROM Rate r")
    List<Object[]> findDistinctCurrencyTitlePairs();

    default Map<String, String> findAllCurrencyNamesWithTitles() {
        return findDistinctCurrencyTitlePairs().stream()
            .collect(Collectors.toMap(
                arr -> (String) arr[0],
                arr -> (String) arr[1],
                (oldVal, newVal) -> oldVal
            ));
    }

}
