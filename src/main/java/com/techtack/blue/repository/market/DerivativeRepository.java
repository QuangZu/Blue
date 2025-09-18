package com.techtack.blue.repository.market;

import com.techtack.blue.model.market.Derivative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DerivativeRepository extends JpaRepository<Derivative, Long> {
    
    Optional<Derivative> findByDerivativeCode(String derivativeCode);
    
    List<Derivative> findByDerivativeType(String derivativeType);
    
    List<Derivative> findByUnderlyingSymbol(String underlyingSymbol);
    
    @Query("SELECT d FROM Derivative d WHERE d.isActive = true ORDER BY d.derivativeCode")
    List<Derivative> findAllActive();
    
    @Query("SELECT d FROM Derivative d WHERE d.expiryDate >= :date AND d.isActive = true ORDER BY d.expiryDate, d.derivativeCode")
    List<Derivative> findActiveNotExpired(@Param("date") LocalDate date);
    
    @Query("SELECT d FROM Derivative d WHERE d.derivativeType = 'FUTURE' AND d.isActive = true ORDER BY d.expiryDate, d.derivativeCode")
    List<Derivative> findAllFutures();
    
    @Query("SELECT d FROM Derivative d WHERE d.derivativeType = 'OPTION' AND d.isActive = true ORDER BY d.expiryDate, d.strikePrice")
    List<Derivative> findAllOptions();
}
