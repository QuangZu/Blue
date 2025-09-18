package com.techtack.blue.service.market;

import com.techtack.blue.dto.market.*;
import com.techtack.blue.model.market.Industry;
import com.techtack.blue.model.market.MarketData;
import com.techtack.blue.model.market.MarketIndex;
import com.techtack.blue.repository.market.IndustryRepository;
import com.techtack.blue.repository.market.MarketDataRepository;
import com.techtack.blue.repository.market.MarketIndexRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarketSnapshotService {
    
    private final MarketDataRepository marketDataRepository;
    private final MarketIndexRepository marketIndexRepository;
    private final IndustryRepository industryRepository;
    private final MarketCalculationService calculationService;
    
    /**
     * Get latest market data for an exchange
     */
    public List<MarketData> getLatestMarketData(String exchange) {
        if (exchange != null) {
            return marketDataRepository.findLatestByExchange(exchange);
        }
        // If no exchange specified, get all latest data
        return marketDataRepository.findLatestByExchange("HOSE");
    }
    
    /**
     * Get complete market snapshot
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "marketSnapshot", key = "#exchange")
    public MarketSnapshotDTO getMarketSnapshot(String exchange) {
        LocalDateTime now = LocalDateTime.now();
        
        // Get latest market data
        List<MarketData> marketDataList = marketDataRepository.findLatestByExchange(exchange);
        
        // Build snapshot
        MarketSnapshotDTO snapshot = new MarketSnapshotDTO();
        snapshot.setExchange(exchange);
        snapshot.setTimestamp(now);
        
        // Set indices
        snapshot.setIndices(getMarketIndices(exchange));
        
        // Set net flow
        snapshot.setNetFlow(getNetFlow(marketDataList));
        
        // Set major impact stocks
        snapshot.setMajorImpactStocks(getMajorImpactStocks(marketDataList, 10));
        
        // Set market cash flow
        snapshot.setCashFlow(getMarketCashFlow(marketDataList));
        
        // Set liquidity
        snapshot.setLiquidity(getMarketLiquidity(marketDataList));
        
        // Set industry heatmap
        snapshot.setIndustryHeatmap(getIndustryHeatmap(exchange));
        
        // Set top foreign trading
        snapshot.setTopForeignTrading(getTopForeignTrading(marketDataList, 10));
        
        // Set top volume
        snapshot.setTopVolume(getTopVolume(marketDataList, 10));
        
        // Set top gainers and losers
        snapshot.setTopGainers(getTopGainers(marketDataList, 10));
        snapshot.setTopLosers(getTopLosers(marketDataList, 10));
        
        // Set market breadth
        Map<String, Integer> breadth = calculationService.calculateMarketBreadth(marketDataList);
        snapshot.setAdvances(breadth.get("advances"));
        snapshot.setDeclines(breadth.get("declines"));
        snapshot.setUnchanged(breadth.get("unchanged"));
        
        return snapshot;
    }
    
    /**
     * Get all market indices
     */
    @Cacheable(value = "marketIndices", key = "#exchange")
    public List<MarketIndexDTO> getMarketIndices(String exchange) {
        List<MarketIndex> indices = exchange != null 
            ? marketIndexRepository.findByExchange(exchange)
            : marketIndexRepository.findAll();
            
        return indices.stream()
            .map(this::convertToIndexDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get net flow data
     */
    public NetFlowDTO getNetFlow(List<MarketData> marketDataList) {
        NetFlowDTO netFlow = new NetFlowDTO();
        
        if (marketDataList == null || marketDataList.isEmpty()) {
            netFlow.setForeignBuyValue(BigDecimal.ZERO);
            netFlow.setForeignSellValue(BigDecimal.ZERO);
            netFlow.setNetValue(BigDecimal.ZERO);
            netFlow.setTimestamp(LocalDateTime.now());
            return netFlow;
        }
        
        BigDecimal foreignBuy = marketDataList.stream()
            .map(MarketData::getForeignBuyValue)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        BigDecimal foreignSell = marketDataList.stream()
            .map(MarketData::getForeignSellValue)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        BigDecimal netValue = foreignBuy.subtract(foreignSell);
        
        netFlow.setForeignBuyValue(foreignBuy);
        netFlow.setForeignSellValue(foreignSell);
        netFlow.setNetValue(netValue);
        netFlow.setTimestamp(LocalDateTime.now());
        
        return netFlow;
    }
    
    /**
     * Get major impact stocks
     */
    public List<StockImpactDTO> getMajorImpactStocks(List<MarketData> marketDataList, int limit) {
        if (marketDataList == null || marketDataList.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<MarketData> impactStocks = calculationService.findMajorImpactStocks(marketDataList, limit);
        
        return impactStocks.stream()
            .map(data -> {
                StockImpactDTO impact = new StockImpactDTO();
                impact.setSymbol(data.getSymbol().getSymbol());
                impact.setCompanyName(data.getSymbol().getCompanyName());
                impact.setCurrentPrice(data.getCurrentPrice());
                impact.setChangePercent(data.getChangePercent());
                impact.setVolume(data.getVolume());
                impact.setValue(data.getValue());
                impact.setWeight(data.getSymbol().getWeight());
                impact.setContribution(calculationService.calculateIndexContribution(
                    data.getSymbol(), data.getChangePercent()));
                return impact;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Get market cash flow
     */
    public CashFlowDTO getMarketCashFlow(List<MarketData> marketDataList) {
        Map<String, BigDecimal> cashFlow = calculationService.calculateMarketCashFlow(marketDataList);
        
        CashFlowDTO dto = new CashFlowDTO();
        dto.setInflow(cashFlow.get("inflow"));
        dto.setOutflow(cashFlow.get("outflow"));
        dto.setNetFlow(cashFlow.get("netFlow"));
        dto.setTimestamp(LocalDateTime.now());
        
        return dto;
    }
    
    /**
     * Get market liquidity
     */
    public LiquidityDTO getMarketLiquidity(List<MarketData> marketDataList) {
        BigDecimal currentLiquidity = calculationService.calculateMarketLiquidity(marketDataList);
        
        // Get 20-day average liquidity
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(20);
        BigDecimal avgLiquidity = marketDataRepository.calculateAverageLiquidity(startDate, endDate);
        
        BigDecimal ratio = calculationService.calculateLiquidityRatio(currentLiquidity, avgLiquidity);
        
        LiquidityDTO liquidity = new LiquidityDTO();
        liquidity.setCurrentLiquidity(currentLiquidity);
        liquidity.setAverageLiquidity(avgLiquidity);
        liquidity.setLiquidityRatio(ratio);
        liquidity.setTimestamp(LocalDateTime.now());
        
        return liquidity;
    }
    
    /**
     * Get industry heatmap data
     */
    @Cacheable(value = "industryHeatmap", key = "#exchange")
    public List<IndustryHeatmapDTO> getIndustryHeatmap(String exchange) {
        List<Industry> industries = industryRepository.findByLevel(1); // Get sectors
        List<IndustryHeatmapDTO> heatmap = new ArrayList<>();
        
        for (Industry industry : industries) {
            List<MarketData> sectorStocks = marketDataRepository.findByIndustryId(industry.getId());
            
            if (!sectorStocks.isEmpty()) {
                IndustryHeatmapDTO dto = new IndustryHeatmapDTO();
                dto.setIndustryCode(industry.getIndustryCode());
                dto.setIndustryName(industry.getIndustryName());
                dto.setIndustryNameEn(industry.getIndustryNameEn());
                
                BigDecimal performance = calculationService.calculateSectorPerformance(sectorStocks);
                dto.setChangePercent(performance);
                
                BigDecimal totalVolume = sectorStocks.stream()
                    .map(MarketData::getVolume)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                dto.setTotalVolume(totalVolume);
                
                BigDecimal totalValue = sectorStocks.stream()
                    .map(MarketData::getValue)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                dto.setTotalValue(totalValue);
                
                Map<String, Integer> breadth = calculationService.calculateMarketBreadth(sectorStocks);
                dto.setAdvances(breadth.get("advances"));
                dto.setDeclines(breadth.get("declines"));
                dto.setUnchanged(breadth.get("unchanged"));
                
                dto.setStockCount(sectorStocks.size());
                
                heatmap.add(dto);
            }
        }
        
        // Sort by absolute change percent
        heatmap.sort((a, b) -> b.getChangePercent().abs().compareTo(a.getChangePercent().abs()));
        
        return heatmap;
    }
    
    /**
     * Get top foreign trading stocks
     */
    public List<ForeignTradingDTO> getTopForeignTrading(List<MarketData> marketDataList, int limit) {
        if (marketDataList == null || marketDataList.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Sort by absolute net foreign value
        List<MarketData> topForeign = marketDataList.stream()
            .filter(data -> data.getForeignNetValue() != null)
            .sorted((a, b) -> b.getForeignNetValue().abs().compareTo(a.getForeignNetValue().abs()))
            .limit(limit)
            .collect(Collectors.toList());
            
        return topForeign.stream()
            .map(data -> {
                ForeignTradingDTO dto = new ForeignTradingDTO();
                dto.setSymbol(data.getSymbol().getSymbol());
                dto.setCompanyName(data.getSymbol().getCompanyName());
                dto.setForeignBuyVolume(data.getForeignBuyVolume());
                dto.setForeignBuyValue(data.getForeignBuyValue());
                dto.setForeignSellVolume(data.getForeignSellVolume());
                dto.setForeignSellValue(data.getForeignSellValue());
                dto.setForeignNetVolume(data.getForeignNetVolume());
                dto.setForeignNetValue(data.getForeignNetValue());
                dto.setCurrentPrice(data.getCurrentPrice());
                dto.setChangePercent(data.getChangePercent());
                return dto;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Get top volume stocks
     */
    @Cacheable(value = "topVolume", key = "#limit")
    public List<StockDataDTO> getTopVolume(List<MarketData> marketDataList, int limit) {
        if (marketDataList == null || marketDataList.isEmpty()) {
            return new ArrayList<>();
        }
        
        return marketDataList.stream()
            .sorted((a, b) -> b.getVolume().compareTo(a.getVolume()))
            .limit(limit)
            .map(this::convertToStockDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get top gainers
     */
    @Cacheable(value = "topGainers", key = "#limit")
    public List<StockDataDTO> getTopGainers(List<MarketData> marketDataList, int limit) {
        if (marketDataList == null || marketDataList.isEmpty()) {
            return new ArrayList<>();
        }
        
        return marketDataList.stream()
            .filter(data -> data.getChangePercent() != null && data.getChangePercent().compareTo(BigDecimal.ZERO) > 0)
            .sorted((a, b) -> b.getChangePercent().compareTo(a.getChangePercent()))
            .limit(limit)
            .map(this::convertToStockDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Get top losers
     */
    @Cacheable(value = "topLosers", key = "#limit")
    public List<StockDataDTO> getTopLosers(List<MarketData> marketDataList, int limit) {
        if (marketDataList == null || marketDataList.isEmpty()) {
            return new ArrayList<>();
        }
        
        return marketDataList.stream()
            .filter(data -> data.getChangePercent() != null && data.getChangePercent().compareTo(BigDecimal.ZERO) < 0)
            .sorted((a, b) -> a.getChangePercent().compareTo(b.getChangePercent()))
            .limit(limit)
            .map(this::convertToStockDTO)
            .collect(Collectors.toList());
    }
    
    // Helper methods
    private MarketIndexDTO convertToIndexDTO(MarketIndex index) {
        MarketIndexDTO dto = new MarketIndexDTO();
        dto.setIndexCode(index.getIndexCode());
        dto.setIndexName(index.getIndexName());
        dto.setExchange(index.getExchange());
        dto.setIndexValue(index.getIndexValue());
        dto.setPreviousClose(index.getPreviousClose());
        dto.setChangeValue(index.getChangeValue());
        dto.setChangePercent(index.getChangePercent());
        dto.setTotalVolume(index.getTotalVolume());
        dto.setTotalValue(index.getTotalValue());
        dto.setAdvances(index.getAdvances());
        dto.setDeclines(index.getDeclines());
        dto.setUnchanged(index.getUnchanged());
        dto.setTimestamp(index.getTradingTime());
        return dto;
    }
    
    private StockDataDTO convertToStockDTO(MarketData data) {
        StockDataDTO dto = new StockDataDTO();
        dto.setSymbol(data.getSymbol().getSymbol());
        dto.setCompanyName(data.getSymbol().getCompanyName());
        dto.setExchange(data.getSymbol().getExchange());
        dto.setCurrentPrice(data.getCurrentPrice());
        dto.setReferencePrice(data.getReferencePrice());
        dto.setOpenPrice(data.getOpenPrice());
        dto.setHighPrice(data.getHighPrice());
        dto.setLowPrice(data.getLowPrice());
        dto.setChangeAmount(data.getChangeAmount());
        dto.setChangePercent(data.getChangePercent());
        dto.setVolume(data.getVolume());
        dto.setValue(data.getValue());
        dto.setMarketCap(data.getSymbol().getMarketCap());
        dto.setTimestamp(data.getTradingTime());
        return dto;
    }
}
