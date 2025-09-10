package com.techtack.blue.dto.market;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketSnapshotDTO {
    private String exchange;
    private LocalDateTime timestamp;
    
    // Market indices
    private List<MarketIndexDTO> indices;
    
    // Net flow
    private NetFlowDTO netFlow;
    
    // Major impact stocks
    private List<StockImpactDTO> majorImpactStocks;
    
    // Cash flow
    private CashFlowDTO cashFlow;
    
    // Liquidity
    private LiquidityDTO liquidity;
    
    // Industry heatmap
    private List<IndustryHeatmapDTO> industryHeatmap;
    
    // Top foreign trading
    private List<ForeignTradingDTO> topForeignTrading;
    
    // Top volume stocks
    private List<StockDataDTO> topVolume;
    
    // Top gainers
    private List<StockDataDTO> topGainers;
    
    // Top losers
    private List<StockDataDTO> topLosers;
    
    // Market breadth
    private Integer advances;
    private Integer declines;
    private Integer unchanged;
}
