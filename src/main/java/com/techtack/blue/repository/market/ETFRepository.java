package com.techtack.blue.repository.market;

import com.techtack.blue.model.market.ETF;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ETFRepository extends JpaRepository<ETF, Long> {
    
    Optional<ETF> findBySymbol(String symbol);
    
    List<ETF> findByFundManager(String fundManager);
    
    List<ETF> findByExchange(String exchange);
    
    List<ETF> findByUnderlyingIndex(String underlyingIndex);
    
    @Query("SELECT e FROM ETF e WHERE e.isActive = true ORDER BY e.symbol")
    List<ETF> findAllActive();
}
