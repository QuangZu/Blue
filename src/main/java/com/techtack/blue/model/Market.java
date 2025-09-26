package com.techtack.blue.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.techtack.blue.model.Stock;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "markets")
@Data
public class Market {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String code;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MarketType type;
    
    @Enumerated(EnumType.STRING)
    private MarketCategory category;
    
    // Basic market data
    private String name;
    private double value;
    private double change;
    private double changePercent;
    private Long volume;
    private LocalDateTime lastUpdated;
    
    // Extended attributes for different market types
    private Double netFlow;
    private Double cashIn;
    private Double cashOut;
    private LocalDate date;
    
    // Industry/Sector specific
    private String industryName;
    private Double marketCap;
    private Integer numberOfStocks;
    
    // Impact and ranking data
    private Double impactValue;
    private Double impactPercentage;
    private Integer rank;
    
    // Foreign trading data
    private Double buyValue;
    private Double sellValue;
    private Double netValue;
    
    // Liquidity data
    private Double liquidityValue;
    private Double normalizedValue;
    
    // Flexible JSON field for additional attributes
    @Column(columnDefinition = "TEXT")
    private String additionalData; // JSON string for extra attributes
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_market_id")
    private Market parentMarket;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_stock_id")
    private Stock relatedStock;
    
    public enum MarketType {
        INDEX,           // Market indices (VN-INDEX, HNX-INDEX)
        NET_FLOW,        // Market net flow data
        INDUSTRY_HEATMAP, // Industry sector data
        MAJOR_IMPACT,    // Major impact stocks
        MARKET_MOVER,    // Market movers (gainers/losers)
        FOREIGN_TRADING, // Foreign trading data
        LIQUIDITY        // Market liquidity data
    }
    
    public enum MarketCategory {
        MAIN_INDEX,      // Main market indices
        SECTOR,          // Industry sectors
        TOP_GAINER,      // Top gaining stocks
        TOP_LOSER,       // Top losing stocks
        MOST_ACTIVE,     // Most active stocks
        FOREIGN_BUY,     // Foreign buying
        FOREIGN_SELL,    // Foreign selling
        HIGH_LIQUIDITY,  // High liquidity
        LOW_LIQUIDITY    // Low liquidity
    }
    
    // Constructors
    public Market() {}
    
    public Market(String code, MarketType type) {
        this.code = code;
        this.type = type;
        this.lastUpdated = LocalDateTime.now();
    }
    
    // Helper methods for different market types
    public boolean isIndex() {
        return type == MarketType.INDEX;
    }
    
    public boolean isNetFlow() {
        return type == MarketType.NET_FLOW;
    }
    
    public boolean isIndustryHeatmap() {
        return type == MarketType.INDUSTRY_HEATMAP;
    }
    
    public boolean isMajorImpact() {
        return type == MarketType.MAJOR_IMPACT;
    }
    
    public boolean isMarketMover() {
        return type == MarketType.MARKET_MOVER;
    }
    
    public boolean isForeignTrading() {
        return type == MarketType.FOREIGN_TRADING;
    }
    
    public boolean isLiquidity() {
        return type == MarketType.LIQUIDITY;
    }
    
    // Utility methods for data access
    public Double getRelevantValue() {
        switch (type) {
            case INDEX:
                return value;
            case NET_FLOW:
                return netFlow;
            case INDUSTRY_HEATMAP:
                return marketCap;
            case MAJOR_IMPACT:
                return impactValue;
            case MARKET_MOVER:
                return value;
            case FOREIGN_TRADING:
                return netValue;
            case LIQUIDITY:
                return liquidityValue;
            default:
                return value;
        }
    }
    
    public String getDisplayName() {
        if (name != null && !name.isEmpty()) {
            return name;
        }
        if (industryName != null && !industryName.isEmpty()) {
            return industryName;
        }
        return code;
    }
}