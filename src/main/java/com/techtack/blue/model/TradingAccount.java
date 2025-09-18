package com.techtack.blue.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trading_accounts")
@Data
public class TradingAccount {

    public enum AccountType {
        CASH,
        MARGIN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private String accountNumber;

    @Column(nullable = false)
    private String accountName;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private BigDecimal totalNAV = BigDecimal.ZERO;
    private BigDecimal totalAssets = BigDecimal.ZERO;
    private BigDecimal cashBalance = BigDecimal.ZERO;
    private BigDecimal buyingPower = BigDecimal.ZERO;
    private BigDecimal withdrawable = BigDecimal.ZERO;
    private BigDecimal right_subscription = BigDecimal.ZERO;
    private BigDecimal stockNAV = BigDecimal.ZERO;
    private BigDecimal fundNAV = BigDecimal.ZERO;
    private BigDecimal investmentProductsNAV = BigDecimal.ZERO;
    private BigDecimal t0buy_value = BigDecimal.ZERO;
    private BigDecimal t0sell_value = BigDecimal.ZERO;
    private BigDecimal t1buy_value = BigDecimal.ZERO;
    private BigDecimal t1sell_value = BigDecimal.ZERO;
    private BigDecimal unmatched_buy_value = BigDecimal.ZERO;
    private BigDecimal unmatched_sell_value = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal availableAdvancedCash = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal cashDividend = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal liabilities = BigDecimal.ZERO;

    private boolean isPrimary;
    private boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}