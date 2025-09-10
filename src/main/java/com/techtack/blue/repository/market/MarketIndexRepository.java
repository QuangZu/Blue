package com.techtack.blue.repository.market;

import com.techtack.blue.model.market.MarketIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MarketIndexRepository extends JpaRepository<MarketIndex, Long> {
    
    Optional<MarketIndex> findByIndexCode(String indexCode);
    
    List<MarketIndex> findByExchange(String exchange);
    
    @Query("SELECT mi FROM MarketIndex mi WHERE mi.tradingTime = " +
           "(SELECT MAX(mi2.tradingTime) FROM MarketIndex mi2 WHERE mi2.indexCode = mi.indexCode)")
    List<MarketIndex> findLatestIndices();
    
    @Query("SELECT mi FROM MarketIndex mi " +
           "WHERE mi.indexCode = :indexCode " +
           "AND mi.tradingTime BETWEEN :startTime AND :endTime " +
           "ORDER BY mi.tradingTime ASC")
    List<MarketIndex> findHistoricalData(@Param("indexCode") String indexCode,
                                         @Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime);
}
