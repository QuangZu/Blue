package com.techtack.blue.model.market;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "market_data", indexes = {
    @Index(name = "idx_market_data_stock_time", columnList = "stock_id, trading_time"),
    @Index(name = "idx_market_data_trading_time", columnList = "trading_time")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private Symbol symbol;
    
    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal openPrice;
    
    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal highPrice;
    
    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal lowPrice;
    
    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal closePrice;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal currentPrice;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal referencePrice; // Previous close
    
    @Column(precision = 12, scale = 2)
    private BigDecimal ceilingPrice;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal floorPrice;
    
    @Column(precision = 18, scale = 0)
    private BigDecimal volume;
    
    @Column(precision = 18, scale = 2)
    private BigDecimal value; // Turnover
    
    @Column(precision = 10, scale = 2)
    private BigDecimal changeAmount;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal changePercent;
    
    // Bid/Ask data
    @Column(precision = 12, scale = 2)
    private BigDecimal bidPrice1;
    
    @Column(precision = 18, scale = 0)
    private BigDecimal bidVolume1;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal bidPrice2;
    
    @Column(precision = 18, scale = 0)
    private BigDecimal bidVolume2;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal bidPrice3;
    
    @Column(precision = 18, scale = 0)
    private BigDecimal bidVolume3;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal askPrice1;
    
    @Column(precision = 18, scale = 0)
    private BigDecimal askVolume1;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal askPrice2;
    
    @Column(precision = 18, scale = 0)
    private BigDecimal askVolume2;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal askPrice3;
    
    @Column(precision = 18, scale = 0)
    private BigDecimal askVolume3;
    
    // Foreign trading
    @Column(precision = 18, scale = 0)
    private BigDecimal foreignBuyVolume;
    
    @Column(precision = 18, scale = 2)
    private BigDecimal foreignBuyValue;
    
    @Column(precision = 18, scale = 0)
    private BigDecimal foreignSellVolume;
    
    @Column(precision = 18, scale = 2)
    private BigDecimal foreignSellValue;
    
    @Column(precision = 18, scale = 0)
    private BigDecimal foreignNetVolume;
    
    @Column(precision = 18, scale = 2)
    private BigDecimal foreignNetValue;
    
    // Proprietary trading
    @Column(precision = 18, scale = 0)
    private BigDecimal proprietaryBuyVolume;
    
    @Column(precision = 18, scale = 2)
    private BigDecimal proprietaryBuyValue;
    
    @Column(precision = 18, scale = 0)
    private BigDecimal proprietarySellVolume;
    
    @Column(precision = 18, scale = 2)
    private BigDecimal proprietarySellValue;
    
    @Column(nullable = false)
    private LocalDateTime tradingTime;
    
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (tradingTime == null) {
            tradingTime = LocalDateTime.now();
        }
    }
}
