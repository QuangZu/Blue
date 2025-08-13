package com.techtack.blue.repository;

import com.techtack.blue.model.Market;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketRepository extends JpaRepository<Market, Long> {
    
    List<Market> findByType(Market.MarketType type);
    
    // Find market by code and type
    Market findByCodeAndType(String code, Market.MarketType type);
    
    // Find markets by parent market and type
    List<Market> findByParentMarketAndType(Market parentMarket, Market.MarketType type);
    
    // Find markets by parent market, type and category ordered by rank
    List<Market> findByParentMarketAndTypeAndCategoryOrderByRankAsc(
        Market parentMarket, 
        Market.MarketType type, 
        Market.MarketCategory category
    );
    
    // Find markets by category
    List<Market> findByCategory(Market.MarketCategory category);
    
    // Find markets by type and category
    List<Market> findByTypeAndCategory(Market.MarketType type, Market.MarketCategory category);
    
    // Find markets by code (regardless of type)
    List<Market> findByCode(String code);
    
    // Custom query to find markets with specific criteria
    @Query("SELECT m FROM Market m WHERE m.type = :type AND m.lastUpdated >= :fromDate")
    List<Market> findByTypeAndLastUpdatedAfter(
        @Param("type") Market.MarketType type, 
        @Param("fromDate") java.time.LocalDateTime fromDate
    );
}