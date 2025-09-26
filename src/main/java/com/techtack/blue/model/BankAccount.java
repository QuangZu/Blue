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
    
    private LocalDateTime updatedAt;

    public void setUser(User user) {
        this.user = user;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public User getUser() {
        return user;
    }
}
