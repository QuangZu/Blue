package com.techtack.blue.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_accounts")
@Data
public class BankAccount {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String accountNumber;
    
    @Column(nullable = false)
    private String bankName;
    
    private String accountHolderName;
    
    @Column(nullable = false)
    private boolean isPrimary = false;
    
    @Column(nullable = false)
    private boolean isVerified = false;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt = LocalDateTime.now();
}
