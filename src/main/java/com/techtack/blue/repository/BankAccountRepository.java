package com.techtack.blue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.techtack.blue.model.BankAccount;
import com.techtack.blue.model.User;
import java.util.List;
import java.util.Optional;

@Repository
public interface
BankAccountRepository extends JpaRepository<BankAccount, Long> {
    
    List<BankAccount> findByUser(User user);
    
    List<BankAccount> findByUserId(Long userId);
    
    Optional<BankAccount> findByUserAndIsPrimaryTrue(User user);
    
    Optional<BankAccount> findByAccountNumberAndBankName(String accountNumber, String bankName);
    
    boolean existsByAccountNumberAndBankName(String accountNumber, String bankName);
}
