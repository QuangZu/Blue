package com.techtack.blue.repository.market;

import com.techtack.blue.model.market.CoveredWarrant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CoveredWarrantRepository extends JpaRepository<CoveredWarrant, Long> {
    
    Optional<CoveredWarrant> findByWarrantCode(String warrantCode);
    
    List<CoveredWarrant> findByUnderlyingSymbol(String underlyingSymbol);
    
    List<CoveredWarrant> findByIssuer(String issuer);
    
    List<CoveredWarrant> findByExchange(String exchange);
    
    @Query("SELECT cw FROM CoveredWarrant cw WHERE cw.isActive = true ORDER BY cw.warrantCode")
    List<CoveredWarrant> findAllActive();
    
    @Query("SELECT cw FROM CoveredWarrant cw WHERE cw.maturityDate >= :date AND cw.isActive = true ORDER BY cw.maturityDate, cw.warrantCode")
    List<CoveredWarrant> findActiveNotExpired(@Param("date") LocalDate date);
    
    @Query("SELECT cw FROM CoveredWarrant cw WHERE cw.warrantType = :type AND cw.isActive = true ORDER BY cw.warrantCode")
    List<CoveredWarrant> findByWarrantType(@Param("type") String type);
}
