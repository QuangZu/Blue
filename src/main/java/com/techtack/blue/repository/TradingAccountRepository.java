package com.techtack.blue.repository;

import com.techtack.blue.model.TradingAccount;
import com.techtack.blue.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TradingAccountRepository extends JpaRepository<TradingAccount, Long> {
    
    List<TradingAccount> findByUserIdAndIsActiveTrue(Long userId);
    
    List<TradingAccount> findByUserAndIsActiveTrue(User user);
    
    Optional<TradingAccount> findByAccountNumberAndIsActiveTrue(String accountNumber);
    
    Optional<TradingAccount> findByUserIdAndAccountTypeAndIsActiveTrue(Long userId, TradingAccount.AccountType accountType);
    
    Optional<TradingAccount> findByUserIdAndIsPrimaryTrueAndIsActiveTrue(Long userId);
    
    @Query("SELECT ta FROM TradingAccount ta WHERE ta.user.id = :userId AND ta.isPrimary = true AND ta.isActive = true")
    Optional<TradingAccount> findPrimaryAccountByUserId(@Param("userId") Long userId);
    
    @Query("SELECT SUM(ta.totalNAV) FROM TradingAccount ta WHERE ta.user.id = :userId AND ta.isActive = true")
    Double getTotalNAVByUserId(@Param("userId") Long userId);
    
    boolean existsByAccountNumber(String accountNumber);
}
