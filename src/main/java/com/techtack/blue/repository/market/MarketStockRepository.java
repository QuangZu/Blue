package com.techtack.blue.repository.market;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techtack.blue.model.Stock;

@Repository
public interface MarketStockRepository extends JpaRepository<Stock, Long> {
    
    Optional<Stock> findBySymbol(String symbol);
    
    // Changed from findByIndustryId to findByIndustry since industry is a String field
    List<Stock> findByIndustry(String industry);
    
    // Note: Stock entity doesn't have isActive field, so we'll just order by symbol
    @Query("SELECT s FROM Stock s ORDER BY s.symbol")
    List<Stock> findAllActive();
    
    @Query("SELECT s FROM Stock s WHERE LOWER(s.symbol) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Stock> searchStocks(@Param("keyword") String keyword);
    
    // Additional method to find stocks by industry name pattern
    @Query("SELECT s FROM Stock s WHERE s.industry LIKE :industryPattern")
    List<Stock> findByIndustryPattern(@Param("industryPattern") String industryPattern);
}
