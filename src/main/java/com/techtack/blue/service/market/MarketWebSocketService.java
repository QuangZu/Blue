package com.techtack.blue.service.market;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.techtack.blue.dto.market.CashFlowDTO;
import com.techtack.blue.dto.market.IndustryHeatmapDTO;
import com.techtack.blue.dto.market.LiquidityDTO;
import com.techtack.blue.dto.market.MarketIndexDTO;
import com.techtack.blue.dto.market.NetFlowDTO;
import com.techtack.blue.dto.market.StockDataDTO;
import com.techtack.blue.model.market.MarketData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarketWebSocketService {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final MarketSnapshotService marketSnapshotService;
    
    @Async
    public void pushMarketUpdate(String topic, Object data) {
        try {
            messagingTemplate.convertAndSend("/topic/" + topic, data);
            log.debug("Pushed update to topic: /topic/{}", topic);
        } catch (Exception e) {
            log.error("Error pushing WebSocket update: ", e);
        }
    }
    
    public void pushStockPriceUpdate(MarketData marketData) {
        StockDataDTO stockData = convertToStockDataDTO(marketData);
        pushMarketUpdate("stock/" + marketData.getSymbol().getSymbol(), stockData);
        pushMarketUpdate("stock/all", stockData);
    }
    
    public void pushIndexUpdate(MarketIndexDTO indexData) {
        pushMarketUpdate("index/" + indexData.getIndexCode(), indexData);
        pushMarketUpdate("index/all", indexData);
    }
    
    public void pushNetFlowUpdate(NetFlowDTO netFlow) {
        pushMarketUpdate("netflow", netFlow);
    }
    
    public void pushIndustryHeatmapUpdate(List<IndustryHeatmapDTO> heatmap) {
        pushMarketUpdate("heatmap", heatmap);
    }
    
    public void pushTopMoversUpdate(String exchange) {
        List<MarketData> marketData = marketSnapshotService.getLatestMarketData(exchange);
        
        // Push top gainers
        List<StockDataDTO> topGainers = marketSnapshotService.getTopGainers(marketData, 10);
        pushMarketUpdate("gainers", topGainers);
        
        // Push top losers
        List<StockDataDTO> topLosers = marketSnapshotService.getTopLosers(marketData, 10);
        pushMarketUpdate("losers", topLosers);
        
        // Push top volume
        List<StockDataDTO> topVolume = marketSnapshotService.getTopVolume(marketData, 10);
        pushMarketUpdate("volume", topVolume);
    }
    
    @Scheduled(fixedDelay = 5000)
    public void pushPeriodicMarketUpdates() {
        if (!isMarketOpen()) {
            return;
        }
        
        try {
            // Push updates for HOSE
            pushExchangeUpdate("HOSE");
            
            // Push updates for HNX
            pushExchangeUpdate("HNX");
            
            // Push updates for UPCOM
            pushExchangeUpdate("UPCOM");
            
        } catch (Exception e) {
            log.error("Error in periodic market updates: ", e);
        }
    }
    
    private void pushExchangeUpdate(String exchange) {
        List<MarketData> marketData = marketSnapshotService.getLatestMarketData(exchange);
        
        // Push net flow
        NetFlowDTO netFlow = marketSnapshotService.getNetFlow(marketData);
        pushMarketUpdate("netflow/" + exchange, netFlow);
        
        // Push market indices
        List<MarketIndexDTO> indices = marketSnapshotService.getMarketIndices(exchange);
        pushMarketUpdate("indices/" + exchange, indices);
        
        // Push liquidity
        LiquidityDTO liquidity = marketSnapshotService.getMarketLiquidity(marketData);
        pushMarketUpdate("liquidity/" + exchange, liquidity);
        
        // Push cash flow
        CashFlowDTO cashFlow = marketSnapshotService.getMarketCashFlow(marketData);
        pushMarketUpdate("cashflow/" + exchange, cashFlow);
    }
    
    private boolean isMarketOpen() {
        // Vietnamese market hours: 9:00 AM - 3:00 PM (ICT)
        java.time.LocalTime now = java.time.LocalTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh"));
        java.time.LocalTime marketOpen = java.time.LocalTime.of(9, 0);
        java.time.LocalTime marketClose = java.time.LocalTime.of(15, 0);
        
        // Check if it's a weekday
        java.time.DayOfWeek dayOfWeek = java.time.LocalDate.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh")).getDayOfWeek();
        boolean isWeekday = dayOfWeek != java.time.DayOfWeek.SATURDAY && dayOfWeek != java.time.DayOfWeek.SUNDAY;
        
        return isWeekday && now.isAfter(marketOpen) && now.isBefore(marketClose);
    }
    
    private StockDataDTO convertToStockDataDTO(MarketData data) {
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
