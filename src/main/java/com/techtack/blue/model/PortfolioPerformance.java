package com.techtack.blue.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "portfolio_performance")
@Data
public class PortfolioPerformance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "trading_account_id", nullable = false)
    private TradingAccount tradingAccount;
    
    @Column(nullable = false)
    private LocalDate performanceDate;
    
    @Column(nullable = false)
    private Double netAssetValue = 0.0;
    
    @Column(nullable = false)
    private Double totalAssetValue = 0.0;
    
    @Column(nullable = false)
    private Double dailyPnL = 0.0; // Daily Profit & Loss
    
    @Column(nullable = false)
    private Double dailyPnLPercent = 0.0;
    
    @Column(nullable = false)
    private Double monthlyPnL = 0.0;
    
    @Column(nullable = false)
    private Double monthlyPnLPercent = 0.0;
    
    @Column(nullable = false)
    private Double yearlyPnL = 0.0;
    
    @Column(nullable = false)
    private Double yearlyPnLPercent = 0.0;
    
    @Column(nullable = false)
    private Double yearToDatePnL = 0.0;
    
    @Column(nullable = false)
    private Double yearToDatePnLPercent = 0.0;
    
    @Column(nullable = false)
    private Double totalCost = 0.0;
    
    @Column(nullable = false)
    private Double totalValue = 0.0;
    
    @Column(nullable = false)
    private Double totalPnL = 0.0;
    
    @Column(nullable = false)
    private Double totalPnLPercent = 0.0;
    
    // Portfolio metrics
    @Column(nullable = false)
    private Double rateOfReturn = 0.0;
    
    @Column(nullable = false)
    private Double benchmarkReturn = 0.0; // VNINDEX return
    
    @Column(nullable = false)
    private Double alpha = 0.0; // Excess return over benchmark
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
