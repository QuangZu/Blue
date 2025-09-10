package com.techtack.blue.model.market;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "symbols", indexes = {
    @Index(name = "idx_stock_symbol", columnList = "symbol"),
    @Index(name = "idx_stock_exchange", columnList = "exchange"),
    @Index(name = "idx_stock_industry", columnList = "industry_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Symbol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 10)
    private String symbol;
    
    @Column(nullable = false)
    private String companyName;
    
    @Column(nullable = false, length = 10)
    private String exchange; // HOSE, HNX, UPCOM
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "industry_id")
    private Industry industry;
    
    @Column(precision = 18, scale = 2)
    private BigDecimal marketCap;
    
    @Column(precision = 18, scale = 0)
    private BigDecimal outstandingShares;
    
    @Column(precision = 18, scale = 0)
    private BigDecimal freeFloat;
    
    @Column(precision = 10, scale = 4)
    private BigDecimal weight;
    
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
