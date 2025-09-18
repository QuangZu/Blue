package com.techtack.blue.model.market;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "etfs", indexes = {
    @Index(name = "idx_etf_symbol", columnList = "symbol"),
    @Index(name = "idx_etf_exchange", columnList = "exchange")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ETF {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 20)
    private String symbol;
    
    @Column(nullable = false)
    private String etfName;
    
    @Column(nullable = false)
    private String fundManager;
    
    private String underlyingIndex; // VN30, VNFIN, etc.
    
    @Column(precision = 12, scale = 2)
    private BigDecimal nav; // Net Asset Value
    
    @Column(precision = 12, scale = 2)
    private BigDecimal navPerUnit;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal lastPrice;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal referencePrice;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal openPrice;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal highPrice;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal lowPrice;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal changeAmount;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal changePercent;
    
    @Column(precision = 18, scale = 0)
    private BigDecimal volume;
    
    @Column(precision = 18, scale = 2)
    private BigDecimal value;
    
    @Column(precision = 18, scale = 0)
    private BigDecimal outstandingUnits;
    
    @Column(precision = 18, scale = 2)
    private BigDecimal totalAssets;
    
    @Column(precision = 10, scale = 4)
    private BigDecimal expenseRatio; // Annual expense ratio
    
    @Column(precision = 10, scale = 4)
    private BigDecimal trackingError;
    
    private LocalDate inceptionDate;
    
    @Column(length = 10)
    private String exchange; // HOSE, HNX
    
    private Boolean isActive;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
