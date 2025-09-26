package com.techtack.blue.dto.market;

import com.techtack.blue.dto.StockDto;
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
    private List<StockDto> topVolume;
    
    // Top gainers
    private List<StockDto> topGainers;
    
    // Top losers
    private List<StockDto> topLosers;
    
    // Market breadth
    private Integer advances;
    private Integer declines;
    private Integer unchanged;

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setTopGainers(List<StockDto> topGainers) {
        this.topGainers = topGainers;
    }

    public void setTopLosers(List<StockDto> topLosers) {
        this.topLosers = topLosers;
    }

    public void setTopVolume(List<StockDto> topVolume) {
        this.topVolume = topVolume;
    }

    public void setAdvances(Integer advances) {
        this.advances = advances;
    }

    public void setDeclines(Integer declines) {
        this.declines = declines;
    }

    public void setUnchanged(Integer unchanged) {
        this.unchanged = unchanged;
    }

    public LiquidityDTO getLiquidity() {
        return liquidity;
    }

    public CashFlowDTO getCashFlow() {
        return cashFlow;
    }
}
