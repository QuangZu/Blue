package com.techtack.blue.repository.market;

import com.techtack.blue.model.market.Symbol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SymbolRepository extends JpaRepository<Symbol, Long> {
    
    Optional<Symbol> findBySymbol(String symbol);
    
    List<Symbol> findByExchange(String exchange);
    
    Page<Symbol> findByExchange(String exchange, Pageable pageable);
    
    List<Symbol> findByIndustry_Id(Long industryId);
    
    @Query("SELECT s FROM Symbol s WHERE s.isActive = true ORDER BY s.symbol")
    List<Symbol> findAllActive();
    
    @Query("SELECT s FROM Symbol s WHERE s.isActive = true AND s.exchange = :exchange ORDER BY s.symbol")
    List<Symbol> findActiveByExchange(@Param("exchange") String exchange);
    
    @Query("SELECT s FROM Symbol s WHERE LOWER(s.symbol) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(s.companyName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Symbol> searchStocks(@Param("keyword") String keyword);
}
