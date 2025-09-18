package com.techtack.blue.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techtack.blue.model.Market;

@Repository
public interface MarketRepository extends JpaRepository<Market, Long> {
    
    List<Market> findByType(Market.MarketType type);
    
    Market findByCodeAndType(String code, Market.MarketType type);
    
    List<Market> findByParentMarketAndType(Market parentMarket, Market.MarketType type);
    
    List<Market> findByParentMarketAndTypeAndCategoryOrderByRankAsc(
        Market parentMarket, 
        Market.MarketType type, 
        Market.MarketCategory category
    );
    
    List<Market> findByCategory(Market.MarketCategory category);
    
    List<Market> findByTypeAndCategory(Market.MarketType type, Market.MarketCategory category);
    
    List<Market> findByCode(String code);

    @Query("SELECT m FROM Market m WHERE m.type = :type AND m.lastUpdated >= :fromDate")
    List<Market> findByTypeAndLastUpdatedAfter(
        @Param("type") Market.MarketType type, 
        @Param("fromDate") java.time.LocalDateTime fromDate
    );
}