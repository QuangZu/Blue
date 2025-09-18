package com.techtack.blue.service;

import com.techtack.blue.dto.MarketDto;
import com.techtack.blue.dto.market.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarketService {
    
    private final AlphaVantageService alphaVantageService;

    @Cacheable(value = "unifiedMarketSnapshot", key = "#exchange", unless = "#result == null")
    public MarketSnapshotDTO getCompleteMarketSnapshot(String exchange) {
        log.info("Fetching complete market snapshot for exchange: {}", exchange);
        
        try {
            // Get base market overview from AlphaVantage
            MarketSnapshotDTO snapshot = alphaVantageService.getMarketOverview();
            
            // Override exchange if specified
            if (exchange != null && !exchange.isEmpty()) {
                snapshot.setExchange(exchange);
            }
            
            // Enhance with sector performance (heatmap)
            List<IndustryHeatmapDTO> sectorPerformance = alphaVantageService.getSectorPerformance();
            snapshot.setIndustryHeatmap(sectorPerformance);
            
            // Calculate market breadth from available data
            calculateMarketBreadth(snapshot);
            
            // Add cash flow estimation based on volume and price changes
            estimateCashFlow(snapshot);
            
            // Add liquidity metrics
            estimateLiquidity(snapshot);
            
            // Add net flow based on top movers
            estimateNetFlow(snapshot);
            
            return snapshot;
            
        } catch (Exception e) {
            log.error("Error fetching market snapshot: ", e);
            // Return a minimal snapshot with error indication
            return createErrorSnapshot(exchange, e.getMessage());
        }
    }
    
    /**
     * Get market indices with real-time data
     */
    @Cacheable(value = "unifiedMarketIndices", unless = "#result == null || #result.isEmpty()")
    public List<MarketIndexDTO> getMarketIndices(String exchange) {
        log.info("Fetching market indices for exchange: {}", exchange);
        
        try {
            List<MarketIndexDTO> indices = alphaVantageService.getMajorIndices();
            
            return indices;
        } catch (Exception e) {
            log.error("Error fetching market indices: ", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Get specific market index by code
     */
    public MarketDto getMarketIndexByCode(String code) {
        log.info("Fetching market index for code: {}", code);
        
        try {
            // Try to get from AlphaVantage first
            List<MarketIndexDTO> indices = alphaVantageService.getMajorIndices();
            
            MarketIndexDTO index = indices.stream()
                .filter(i -> i.getIndexCode().equalsIgnoreCase(code))
                .findFirst()
                .orElse(null);
                
            if (index != null) {
                return convertIndexToMarketDto(index);
            }
            
            // If not found, try to fetch directly
            StockDataDTO stockData = alphaVantageService.getDetailedStockQuote(code);
            if (stockData != null) {
                return convertStockToMarketDto(stockData);
            }
            
            return null;
        } catch (Exception e) {
            log.error("Error fetching market index by code: ", e);
            return null;
        }
    }
    
    /**
     * Get top gainers
     */
    @Cacheable(value = "unifiedTopGainers", key = "#exchange + '_' + #limit", unless = "#result == null || #result.isEmpty()")
    public List<StockDataDTO> getTopGainers(String exchange, int limit) {
        log.info("Fetching top {} gainers for exchange: {}", limit, exchange);
        
        try {
            return alphaVantageService.getTopGainers(limit);
        } catch (Exception e) {
            log.error("Error fetching top gainers: ", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Get top losers
     */
    @Cacheable(value = "unifiedTopLosers", key = "#exchange + '_' + #limit", unless = "#result == null || #result.isEmpty()")
    public List<StockDataDTO> getTopLosers(String exchange, int limit) {
        log.info("Fetching top {} losers for exchange: {}", limit, exchange);
        
        try {
            return alphaVantageService.getTopLosers(limit);
        } catch (Exception e) {
            log.error("Error fetching top losers: ", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Get most active stocks by volume
     */
    @Cacheable(value = "unifiedTopVolume", key = "#exchange + '_' + #limit", unless = "#result == null || #result.isEmpty()")
    public List<StockDataDTO> getTopVolume(String exchange, int limit) {
        log.info("Fetching top {} volume stocks for exchange: {}", limit, exchange);
        
        try {
            return alphaVantageService.getMostActiveStocks(limit);
        } catch (Exception e) {
            log.error("Error fetching top volume stocks: ", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Get market movers based on type
     */
    public List<MarketDto> getMarketMovers(String marketCode, String moverType) {
        log.info("Fetching market movers - market: {}, type: {}", marketCode, moverType);
        
        try {
            List<StockDataDTO> stocks = null;
            
            switch (moverType.toUpperCase()) {
                case "TOP_GAINER":
                case "TOP_GAINERS":
                    stocks = alphaVantageService.getTopGainers(10);
                    break;
                case "TOP_LOSER":
                case "TOP_LOSERS":
                    stocks = alphaVantageService.getTopLosers(10);
                    break;
                case "MOST_ACTIVE":
                case "TOP_VOLUME":
                    stocks = alphaVantageService.getMostActiveStocks(10);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid mover type: " + moverType);
            }
            
            if (stocks != null) {
                return stocks.stream()
                    .map(this::convertStockToMarketDto)
                    .collect(Collectors.toList());
            }
            
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Error fetching market movers: ", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Get industry heatmap
     */
    @Cacheable(value = "unifiedIndustryHeatmap", key = "#exchange", unless = "#result == null || #result.isEmpty()")
    public List<IndustryHeatmapDTO> getIndustryHeatmap(String exchange) {
        log.info("Fetching industry heatmap for exchange: {}", exchange);
        
        try {
            return alphaVantageService.getSectorPerformance();
        } catch (Exception e) {
            log.error("Error fetching industry heatmap: ", e);
            return Collections.emptyList();
        }
    }
    
    /**
     * Get market statistics
     */
    public Map<String, Object> getMarketStatistics(String exchange) {
        log.info("Fetching market statistics for exchange: {}", exchange);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("exchange", exchange);
        stats.put("timestamp", LocalDateTime.now());
        
        try {
            // Get top gainers and losers to calculate breadth
            List<StockDataDTO> gainers = alphaVantageService.getTopGainers(100);
            List<StockDataDTO> losers = alphaVantageService.getTopLosers(100);
            List<StockDataDTO> active = alphaVantageService.getMostActiveStocks(100);
            
            int advances = gainers.size();
            int declines = losers.size();
            int totalStocks = advances + declines;
            
            stats.put("totalStocks", totalStocks);
            stats.put("advances", advances);
            stats.put("declines", declines);
            stats.put("unchanged", 0); // Can't determine from API
            stats.put("advanceDeclineRatio", declines > 0 ? (double) advances / declines : advances);
            
            // Calculate total volume from active stocks
            BigDecimal totalVolume = active.stream()
                .map(StockDataDTO::getVolume)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            stats.put("totalVolume", totalVolume);
            
            // Get market indices
            List<MarketIndexDTO> indices = alphaVantageService.getMajorIndices();
            stats.put("indices", indices);
            
        } catch (Exception e) {
            log.error("Error calculating market statistics: ", e);
            stats.put("error", e.getMessage());
        }
        
        return stats;
    }
    
    /**
     * Get market net flow (estimated from foreign trading simulation)
     */
    public List<MarketDto> getMarketNetFlow(String marketCode) {
        log.info("Fetching market net flow for: {}", marketCode);
        
        List<MarketDto> netFlows = new ArrayList<>();
        
        try {
            // Since AlphaVantage doesn't provide foreign trading data,
            // we'll simulate it based on top movers
            List<StockDataDTO> topStocks = alphaVantageService.getMostActiveStocks(20);
            
            for (StockDataDTO stock : topStocks) {
                MarketDto flow = new MarketDto();
                flow.setCode(stock.getSymbol());
                flow.setName(stock.getCompanyName());
                flow.setVolume(stock.getVolume() != null ? stock.getVolume().longValue() : 0L);
                
                // Simulate net flow based on price change
                if (stock.getChangePercent() != null && stock.getVolume() != null && stock.getCurrentPrice() != null) {
                    BigDecimal netFlow = stock.getVolume()
                        .multiply(stock.getCurrentPrice())
                        .multiply(stock.getChangePercent())
                        .divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
                    flow.setNetFlow(netFlow.doubleValue());
                    
                    if (netFlow.compareTo(BigDecimal.ZERO) > 0) {
                        flow.setCashIn(netFlow.abs().doubleValue());
                        flow.setCashOut(0.0);
                    } else {
                        flow.setCashIn(0.0);
                        flow.setCashOut(netFlow.abs().doubleValue());
                    }
                }
                
                flow.setLastUpdated(LocalDateTime.now());
                netFlows.add(flow);
            }
            
        } catch (Exception e) {
            log.error("Error fetching market net flow: ", e);
        }
        
        return netFlows;
    }
    
    // Helper methods
    
    private void calculateMarketBreadth(MarketSnapshotDTO snapshot) {
        if (snapshot.getTopGainers() != null && snapshot.getTopLosers() != null) {
            int advances = snapshot.getTopGainers().size();
            int declines = snapshot.getTopLosers().size();
            
            snapshot.setAdvances(advances);
            snapshot.setDeclines(declines);
            snapshot.setUnchanged(0); // Can't determine from available data
        }
    }
    
    private void estimateCashFlow(MarketSnapshotDTO snapshot) {
        CashFlowDTO cashFlow = new CashFlowDTO();
        cashFlow.setTimestamp(LocalDateTime.now());
        
        BigDecimal inflow = BigDecimal.ZERO;
        BigDecimal outflow = BigDecimal.ZERO;
        
        // Calculate from top gainers (inflow)
        if (snapshot.getTopGainers() != null) {
            for (StockDataDTO stock : snapshot.getTopGainers()) {
                if (stock.getVolume() != null && stock.getCurrentPrice() != null) {
                    BigDecimal value = stock.getVolume().multiply(stock.getCurrentPrice());
                    inflow = inflow.add(value);
                }
            }
        }
        
        // Calculate from top losers (outflow)
        if (snapshot.getTopLosers() != null) {
            for (StockDataDTO stock : snapshot.getTopLosers()) {
                if (stock.getVolume() != null && stock.getCurrentPrice() != null) {
                    BigDecimal value = stock.getVolume().multiply(stock.getCurrentPrice());
                    outflow = outflow.add(value);
                }
            }
        }
        
        cashFlow.setInflow(inflow);
        cashFlow.setOutflow(outflow);
        cashFlow.setNetFlow(inflow.subtract(outflow));
        
        snapshot.setCashFlow(cashFlow);
    }
    
    private void estimateLiquidity(MarketSnapshotDTO snapshot) {
        LiquidityDTO liquidity = new LiquidityDTO();
        liquidity.setTimestamp(LocalDateTime.now());
        
        BigDecimal totalLiquidity = BigDecimal.ZERO;
        
        // Calculate from top volume stocks
        if (snapshot.getTopVolume() != null) {
            for (StockDataDTO stock : snapshot.getTopVolume()) {
                if (stock.getVolume() != null && stock.getCurrentPrice() != null) {
                    BigDecimal value = stock.getVolume().multiply(stock.getCurrentPrice());
                    totalLiquidity = totalLiquidity.add(value);
                }
            }
        }
        
        liquidity.setCurrentLiquidity(totalLiquidity);
        liquidity.setAverageLiquidity(totalLiquidity); // Can't calculate historical average from API
        liquidity.setLiquidityRatio(BigDecimal.ONE); // Default ratio
        
        snapshot.setLiquidity(liquidity);
    }
    
    private void estimateNetFlow(MarketSnapshotDTO snapshot) {
        NetFlowDTO netFlow = new NetFlowDTO();
        netFlow.setTimestamp(LocalDateTime.now());
        
        // Simulate foreign trading based on market movement
        BigDecimal totalBuy = BigDecimal.ZERO;
        BigDecimal totalSell = BigDecimal.ZERO;
        
        if (snapshot.getTopGainers() != null) {
            for (StockDataDTO stock : snapshot.getTopGainers()) {
                if (stock.getVolume() != null && stock.getCurrentPrice() != null) {
                    BigDecimal value = stock.getVolume().multiply(stock.getCurrentPrice())
                        .multiply(new BigDecimal("0.3")); // Assume 30% foreign participation
                    totalBuy = totalBuy.add(value);
                }
            }
        }
        
        if (snapshot.getTopLosers() != null) {
            for (StockDataDTO stock : snapshot.getTopLosers()) {
                if (stock.getVolume() != null && stock.getCurrentPrice() != null) {
                    BigDecimal value = stock.getVolume().multiply(stock.getCurrentPrice())
                        .multiply(new BigDecimal("0.3")); // Assume 30% foreign participation
                    totalSell = totalSell.add(value);
                }
            }
        }
        
        netFlow.setForeignBuyValue(totalBuy);
        netFlow.setForeignSellValue(totalSell);
        netFlow.setNetValue(totalBuy.subtract(totalSell));
        
        snapshot.setNetFlow(netFlow);
    }
    
    private MarketDto convertIndexToMarketDto(MarketIndexDTO index) {
        MarketDto dto = new MarketDto();
        dto.setCode(index.getIndexCode());
        dto.setName(index.getIndexName());
        dto.setValue(index.getIndexValue() != null ? index.getIndexValue().doubleValue() : 0.0);
        dto.setChange(index.getChangeValue() != null ? index.getChangeValue().doubleValue() : 0.0);
        dto.setChangePercent(index.getChangePercent() != null ? index.getChangePercent().doubleValue() : 0.0);
        dto.setVolume(index.getTotalVolume() != null ? index.getTotalVolume().longValue() : 0L);
        dto.setLastUpdated(index.getTimestamp());
        dto.setType("INDEX");
        return dto;
    }
    
    private MarketDto convertStockToMarketDto(StockDataDTO stock) {
        MarketDto dto = new MarketDto();
        dto.setCode(stock.getSymbol());
        dto.setName(stock.getCompanyName());
        dto.setValue(stock.getCurrentPrice() != null ? stock.getCurrentPrice().doubleValue() : 0.0);
        dto.setChange(stock.getChangeAmount() != null ? stock.getChangeAmount().doubleValue() : 0.0);
        dto.setChangePercent(stock.getChangePercent() != null ? stock.getChangePercent().doubleValue() : 0.0);
        dto.setVolume(stock.getVolume() != null ? stock.getVolume().longValue() : 0L);
        dto.setLastUpdated(stock.getTimestamp());
        dto.setType("STOCK");
        return dto;
    }
    
    private MarketSnapshotDTO createErrorSnapshot(String exchange, String errorMessage) {
        MarketSnapshotDTO snapshot = new MarketSnapshotDTO();
        snapshot.setExchange(exchange);
        snapshot.setTimestamp(LocalDateTime.now());
        snapshot.setIndices(Collections.emptyList());
        snapshot.setTopGainers(Collections.emptyList());
        snapshot.setTopLosers(Collections.emptyList());
        snapshot.setTopVolume(Collections.emptyList());
        snapshot.setAdvances(0);
        snapshot.setDeclines(0);
        snapshot.setUnchanged(0);
        
        log.error("Created error snapshot due to: {}", errorMessage);
        return snapshot;
    }
}
