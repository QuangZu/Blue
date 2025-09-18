package com.techtack.blue.model.market;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "market_indices", indexes = {
    @Index(name = "idx_index_code", columnList = "indexCode"),
    @Index(name = "idx_index_time", columnList = "tradingTime")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketIndex {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 20)
    private String indexCode; // VN-INDEX, HNX-INDEX, VN30, HNX30, UPCOM
    
    @Column(nullable = false)
    private String indexName;
    
    @Column(length = 10)
    private String exchange; // HOSE, HNX, UPCOM
    
    @Column(precision = 12, scale = 2)
    private BigDecimal indexValue;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal previousClose;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal openIndex;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal highIndex;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal lowIndex;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal changeValue;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal changePercent;
    
    @Column(precision = 18, scale = 0)
    private BigDecimal totalVolume;
    
    @Column(precision = 18, scale = 2)
    private BigDecimal totalValue;
    
    private Integer advances; // Number of advancing stocks
    
    private Integer declines; // Number of declining stocks
    
    private Integer unchanged; // Number of unchanged stocks
    
    // Market statistics
    @Column(precision = 18, scale = 2)
    private BigDecimal marketCap;
    
    @Column(precision = 18, scale = 2)
    private BigDecimal foreignBuyValue;
    
    @Column(precision = 18, scale = 2)
    private BigDecimal foreignSellValue;
    
    @Column(precision = 18, scale = 2)
    private BigDecimal foreignNetValue;
    
    private LocalDateTime tradingTime;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
