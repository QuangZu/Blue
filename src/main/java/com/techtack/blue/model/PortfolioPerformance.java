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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TradingAccount getTradingAccount() {
        return tradingAccount;
    }

    public void setTradingAccount(TradingAccount tradingAccount) {
        this.tradingAccount = tradingAccount;
    }

    public LocalDate getPerformanceDate() {
        return performanceDate;
    }

    public void setPerformanceDate(LocalDate performanceDate) {
        this.performanceDate = performanceDate;
    }

    public Double getNetAssetValue() {
        return netAssetValue;
    }

    public void setNetAssetValue(Double netAssetValue) {
        this.netAssetValue = netAssetValue;
    }

    public Double getTotalAssetValue() {
        return totalAssetValue;
    }

    public void setTotalAssetValue(Double totalAssetValue) {
        this.totalAssetValue = totalAssetValue;
    }

    public Double getDailyPnL() {
        return dailyPnL;
    }

    public void setDailyPnL(Double dailyPnL) {
        this.dailyPnL = dailyPnL;
    }

    public Double getDailyPnLPercent() {
        return dailyPnLPercent;
    }

    public void setDailyPnLPercent(Double dailyPnLPercent) {
        this.dailyPnLPercent = dailyPnLPercent;
    }

    public Double getMonthlyPnL() {
        return monthlyPnL;
    }

    public void setMonthlyPnL(Double monthlyPnL) {
        this.monthlyPnL = monthlyPnL;
    }

    public Double getMonthlyPnLPercent() {
        return monthlyPnLPercent;
    }

    public void setMonthlyPnLPercent(Double monthlyPnLPercent) {
        this.monthlyPnLPercent = monthlyPnLPercent;
    }

    public Double getYearlyPnL() {
        return yearlyPnL;
    }

    public void setYearlyPnL(Double yearlyPnL) {
        this.yearlyPnL = yearlyPnL;
    }

    public Double getYearlyPnLPercent() {
        return yearlyPnLPercent;
    }

    public void setYearlyPnLPercent(Double yearlyPnLPercent) {
        this.yearlyPnLPercent = yearlyPnLPercent;
    }

    public Double getYearToDatePnL() {
        return yearToDatePnL;
    }

    public void setYearToDatePnL(Double yearToDatePnL) {
        this.yearToDatePnL = yearToDatePnL;
    }

    public Double getYearToDatePnLPercent() {
        return yearToDatePnLPercent;
    }

    public void setYearToDatePnLPercent(Double yearToDatePnLPercent) {
        this.yearToDatePnLPercent = yearToDatePnLPercent;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public Double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(Double totalValue) {
        this.totalValue = totalValue;
    }

    public Double getTotalPnL() {
        return totalPnL;
    }

    public void setTotalPnL(Double totalPnL) {
        this.totalPnL = totalPnL;
    }

    public Double getTotalPnLPercent() {
        return totalPnLPercent;
    }

    public void setTotalPnLPercent(Double totalPnLPercent) {
        this.totalPnLPercent = totalPnLPercent;
    }

    public Double getRateOfReturn() {
        return rateOfReturn;
    }

    public void setRateOfReturn(Double rateOfReturn) {
        this.rateOfReturn = rateOfReturn;
    }

    public Double getBenchmarkReturn() {
        return benchmarkReturn;
    }

    public void setBenchmarkReturn(Double benchmarkReturn) {
        this.benchmarkReturn = benchmarkReturn;
    }

    public Double getAlpha() {
        return alpha;
    }

    public void setAlpha(Double alpha) {
        this.alpha = alpha;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
