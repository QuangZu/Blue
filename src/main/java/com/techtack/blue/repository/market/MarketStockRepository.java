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
    
    // Note: Stock entity doesn't have exchange field, so these methods are removed
    // Use Symbol entity for exchange-based queries
    
    List<Stock> findByIndustry(String industry);
    
    // Note: Stock entity doesn't have isActive field, removed findAllActive method
    // Use Symbol entity for active status queries
    
    // Removed: Stock entity doesn't have exchange field
    
    @Query("SELECT s FROM Stock s WHERE LOWER(s.symbol) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Stock> searchStocks(@Param("keyword") String keyword);
}
