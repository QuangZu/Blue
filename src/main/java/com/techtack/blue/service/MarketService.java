package com.techtack.blue.service;

import com.techtack.blue.dto.MarketDto;
import com.techtack.blue.dto.StockDto;
import com.techtack.blue.dto.market.*;
import com.techtack.blue.dto.stock.ThiTruongChungKhoanQuocTeDto;
import com.techtack.blue.dto.stock.ThiTruongChungKhoanVietNamDto;
import com.techtack.blue.service.stock.StockService;
import com.techtack.blue.service.stock.WifeedStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketService {

    private final StockService stockService;
    private final WifeedStockService wifeedStockService;

    @Cacheable(value = "unifiedMarketSnapshot", key = "#exchange", unless = "#result == null")
    public MarketSnapshotDTO getCompleteMarketSnapshot(String exchange) {
        try {
            MarketSnapshotDTO snapshot = new MarketSnapshotDTO();
            snapshot.setExchange(exchange);
            snapshot.setTimestamp(java.time.LocalDateTime.now());

            // Get Vietnam market data
            ThiTruongChungKhoanVietNamDto vnMarket =
                wifeedStockService.getVietnamMarketOverview(1, 10);
            
            // Get International market data
            ThiTruongChungKhoanQuocTeDto intlMarket =
                wifeedStockService.getInternationalMarketOverview(1, 10);

            // Set up indices based on the retrieved data
            if (vnMarket != null) {
                // Create MarketIndexDTOs for Vietnam indices
                java.util.List<MarketIndexDTO> indices = new java.util.ArrayList<>();
                
                if (vnMarket.getVnindex() != null) {
                    MarketIndexDTO vnIndex = new MarketIndexDTO();
                    vnIndex.setIndexName("VN-Index");
                    vnIndex.setIndexValue(vnMarket.getVnindex());
                    vnIndex.setChangeValue(vnMarket.getVnindex().multiply(new java.math.BigDecimal("0.01"))); // placeholder
                    vnIndex.setChangePercent(new java.math.BigDecimal("0.01")); // placeholder
                    indices.add(vnIndex);
                }
                
                if (vnMarket.getHnxindex() != null) {
                    MarketIndexDTO hnxIndex = new MarketIndexDTO();
                    hnxIndex.setIndexName("HNX-Index");
                    hnxIndex.setIndexValue(vnMarket.getHnxindex());
                    hnxIndex.setChangeValue(vnMarket.getHnxindex().multiply(new java.math.BigDecimal("0.01"))); // placeholder
                    hnxIndex.setChangePercent(new java.math.BigDecimal("0.01")); // placeholder
                    indices.add(hnxIndex);
                }
                
                if (vnMarket.getUpindex() != null) {
                    MarketIndexDTO upIndex = new MarketIndexDTO();
                    upIndex.setIndexName("UP-Index");
                    upIndex.setIndexValue(vnMarket.getUpindex());
                    upIndex.setChangeValue(vnMarket.getUpindex().multiply(new java.math.BigDecimal("0.01"))); // placeholder
                    upIndex.setChangePercent(new java.math.BigDecimal("0.01")); // placeholder
                    indices.add(upIndex);
                }
                
                snapshot.setIndices(indices);
            }

            // Add international indices if available
            if (intlMarket != null && snapshot.getIndices() != null) {
                if (intlMarket.getDji() != null) {
                    MarketIndexDTO djiIndex = new MarketIndexDTO();
                    djiIndex.setIndexName("Dow Jones");
                    djiIndex.setIndexValue(intlMarket.getDji());
                    djiIndex.setChangeValue(intlMarket.getDji().multiply(new java.math.BigDecimal("0.01"))); // placeholder
                    djiIndex.setChangePercent(new java.math.BigDecimal("0.01")); // placeholder
                    snapshot.getIndices().add(djiIndex);
                }
                
                if (intlMarket.getSpx() != null) {
                    MarketIndexDTO spxIndex = new MarketIndexDTO();
                    spxIndex.setIndexName("S&P 500");
                    spxIndex.setIndexValue(intlMarket.getSpx());
                    spxIndex.setChangeValue(intlMarket.getSpx().multiply(new java.math.BigDecimal("0.01"))); // placeholder
                    spxIndex.setChangePercent(new java.math.BigDecimal("0.01")); // placeholder
                    snapshot.getIndices().add(spxIndex);
                }
                
                if (intlMarket.getN225() != null) {
                    MarketIndexDTO n225Index = new MarketIndexDTO();
                    n225Index.setIndexName("Nikkei 225");
                    n225Index.setIndexValue(intlMarket.getN225());
                    n225Index.setChangeValue(intlMarket.getN225().multiply(new java.math.BigDecimal("0.01"))); // placeholder
                    n225Index.setChangePercent(new java.math.BigDecimal("0.01")); // placeholder
                    snapshot.getIndices().add(n225Index);
                }
            }

            // Get other market data using existing methods
            java.util.List<StockDto> topGainers = stockService.getTopGainers();
            java.util.List<StockDto> topLosers = stockService.getTopLosers();
            java.util.List<StockDto> topVolume = stockService.getMostActive();

            snapshot.setTopGainers(topGainers);
            snapshot.setTopLosers(topLosers);
            snapshot.setTopVolume(topVolume);

            // Calculate market breadth (advancers/decliners)
            calculateMarketBreadth(snapshot, topGainers, topLosers);

            // Calculate other metrics
            estimateCashFlow(snapshot);
            estimateLiquidity(snapshot);
            estimateNetFlow(snapshot);

            return snapshot;
        } catch (Exception e) {
            e.printStackTrace();
            return createErrorSnapshot(exchange, e.getMessage());
        }
    }
    
    private void calculateMarketBreadth(MarketSnapshotDTO snapshot, 
                                      java.util.List<StockDto> topGainers,
                                      java.util.List<StockDto> topLosers) {
        snapshot.setAdvances(topGainers != null ? topGainers.size() : 0);
        snapshot.setDeclines(topLosers != null ? topLosers.size() : 0);
        snapshot.setUnchanged(0); // Placeholder - would need to calculate separately
    }

    @Cacheable(value = "unifiedMarketIndices", unless = "#result == null || #result.isEmpty()")
    public List<MarketIndexDTO> getMarketIndices(String exchange) {
        return Collections.emptyList();
    }

    private void estimateCashFlow(MarketSnapshotDTO snapshot) {
        CashFlowDTO cashFlow = new CashFlowDTO();
        cashFlow.setTimestamp(LocalDateTime.now());

        BigDecimal inflow = calculateTotalValue(snapshot.getTopGainers());
        BigDecimal outflow = calculateTotalValue(snapshot.getTopLosers());

        cashFlow.setInflow(inflow);
        cashFlow.setOutflow(outflow);
        cashFlow.setNetFlow(inflow.subtract(outflow));
        snapshot.setCashFlow(cashFlow);
    }

    private void estimateLiquidity(MarketSnapshotDTO snapshot) {
        LiquidityDTO liquidity = new LiquidityDTO();
        liquidity.setTimestamp(LocalDateTime.now());

        BigDecimal totalLiquidity = calculateTotalValue(snapshot.getTopVolume());

        liquidity.setCurrentLiquidity(totalLiquidity);
        liquidity.setAverageLiquidity(totalLiquidity);
        liquidity.setLiquidityRatio(BigDecimal.ONE);
        snapshot.setLiquidity(liquidity);
    }

    private void estimateNetFlow(MarketSnapshotDTO snapshot) {
        NetFlowDTO netFlow = new NetFlowDTO();
        netFlow.setTimestamp(LocalDateTime.now());

        BigDecimal totalBuy = calculateTotalValue(snapshot.getTopGainers()).multiply(new BigDecimal("0.3"));
        BigDecimal totalSell = calculateTotalValue(snapshot.getTopLosers()).multiply(new BigDecimal("0.3"));

        netFlow.setForeignBuyValue(totalBuy);
        netFlow.setForeignSellValue(totalSell);
        netFlow.setNetValue(totalBuy.subtract(totalSell));
        snapshot.setNetFlow(netFlow);
    }

    private BigDecimal calculateTotalValue(List<StockDto> stocks) {
        if (stocks == null) {
            return BigDecimal.ZERO;
        }
        return stocks.stream()
                .filter(stock -> stock.getPrice() != null && stock.getVolume() != null)
                .map(stock -> BigDecimal.valueOf(stock.getPrice()).multiply(BigDecimal.valueOf(stock.getVolume())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private MarketDto convertStockToMarketDto(StockDto stock) {
        MarketDto dto = new MarketDto();
        dto.setCode(stock.getCode());
        dto.setName(stock.getName());
        dto.setValue(stock.getPrice());
        dto.setChange(stock.getChangeAmount());
        dto.setChangePercent(stock.getChangePercent());
        dto.setVolume(stock.getVolume());
        dto.setLastUpdated(stock.getLastUpdated());
        dto.setType("STOCK");
        return dto;
    }

    private MarketSnapshotDTO createErrorSnapshot(String exchange, String errorMessage) {
        MarketSnapshotDTO snapshot = new MarketSnapshotDTO();
        snapshot.setExchange(exchange);
        snapshot.setTimestamp(LocalDateTime.now());
        snapshot.setTopGainers(Collections.emptyList());
        snapshot.setTopLosers(Collections.emptyList());
        snapshot.setTopVolume(Collections.emptyList());
        snapshot.setAdvances(0);
        snapshot.setDeclines(0);
        snapshot.setUnchanged(0);
        return snapshot;
    }
}
