package com.techtack.blue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.techtack.blue.model.DepositTransaction;
import com.techtack.blue.model.User;
import java.util.List;
import java.util.Optional;

@Repository
public interface DepositTransactionRepository extends JpaRepository<DepositTransaction, Long> {
    
    List<DepositTransaction> findByUser(User user);
    
    List<DepositTransaction> findByUserId(Long userId);
    
    List<DepositTransaction> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    Optional<DepositTransaction> findByTransactionReference(String transactionReference);
    
    List<DepositTransaction> findByStatus(DepositTransaction.TransactionStatus status);
}
