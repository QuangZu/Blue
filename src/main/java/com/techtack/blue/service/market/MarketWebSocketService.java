package com.techtack.blue.service.market;

import com.techtack.blue.dto.StockDto;
import com.techtack.blue.dto.market.MarketIndexDTO;
import com.techtack.blue.dto.market.MarketSnapshotDTO;
import com.techtack.blue.service.MarketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarketWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;
    private final MarketService marketService;

    @Async
    public void pushMarketUpdate(String topic, Object data) {
        try {
            messagingTemplate.convertAndSend("/topic/" + topic, data);
            log.debug("Pushed update to topic: /topic/{}", topic);
        } catch (Exception e) {
            log.error("Error pushing WebSocket update: ", e);
        }
    }

    public void pushStockPriceUpdate(StockDto stockDto) {
        pushMarketUpdate("stock/" + stockDto.getCode(), stockDto);
        pushMarketUpdate("stock/all", stockDto);
    }

    public void pushIndexUpdate(MarketIndexDTO indexData) {
        pushMarketUpdate("index/" + indexData.getIndexCode(), indexData);
        pushMarketUpdate("index/all", indexData);
    }

    @Scheduled(fixedDelay = 5000)
    public void pushPeriodicMarketUpdates() {
        if (!isMarketOpen()) {
            return;
        }

        try {
            pushExchangeUpdate("HOSE");
        } catch (Exception e) {
            log.error("Error in periodic market updates: ", e);
        }
    }

    private void pushExchangeUpdate(String exchange) {
        MarketSnapshotDTO snapshot = marketService.getCompleteMarketSnapshot(exchange);

        if (snapshot.getNetFlow() != null) {
            pushMarketUpdate("netflow/" + exchange, snapshot.getNetFlow());
        }
        if (snapshot.getIndices() != null) {
            pushMarketUpdate("indices/" + exchange, snapshot.getIndices());
        }
        if (snapshot.getLiquidity() != null) {
            pushMarketUpdate("liquidity/" + exchange, snapshot.getLiquidity());
        }
        if (snapshot.getCashFlow() != null) {
            pushMarketUpdate("cashflow/" + exchange, snapshot.getCashFlow());
        }
    }

    private boolean isMarketOpen() {
        java.time.LocalTime now = java.time.LocalTime.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh"));
        java.time.LocalTime marketOpen = java.time.LocalTime.of(9, 0);
        java.time.LocalTime marketClose = java.time.LocalTime.of(15, 0);
        java.time.DayOfWeek dayOfWeek = java.time.LocalDate.now(java.time.ZoneId.of("Asia/Ho_Chi_Minh")).getDayOfWeek();
        boolean isWeekday = dayOfWeek != java.time.DayOfWeek.SATURDAY && dayOfWeek != java.time.DayOfWeek.SUNDAY;
        return isWeekday && now.isAfter(marketOpen) && now.isBefore(marketClose);
    }
}