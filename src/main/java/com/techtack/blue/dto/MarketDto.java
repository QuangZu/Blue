package com.techtack.blue.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MarketDto {
    private Long id;
    private String code;
    private String type;
    private String category;
    
    // Basic market data
    private String name;
    private Double value;
    private Double change;
    private Double changePercent;
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
    
    // Relationships
    private Long parentMarketId;
    private Long relatedStockId;
    
    // Additional data as string (JSON)
    private String additionalData;
}