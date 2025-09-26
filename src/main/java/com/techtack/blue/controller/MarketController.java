package com.techtack.blue.controller;

import com.techtack.blue.dto.market.CashFlowDTO;
import com.techtack.blue.dto.market.LiquidityDTO;
import com.techtack.blue.dto.market.MarketIndexDTO;
import com.techtack.blue.dto.market.MarketSnapshotDTO;
import com.techtack.blue.service.MarketService;
import com.techtack.blue.service.stock.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/market")
public class MarketController {

    @Autowired
    private MarketService marketService;
    
    @Autowired
    private StockService stockService;

    @GetMapping("/snapshot")
    public ResponseEntity<?> getMarketSnapshot(@RequestParam(required = false, defaultValue = "HOSE") String exchange) {
        try {
            MarketSnapshotDTO snapshot = stockService.getMarketOverview();
            return ResponseEntity.ok(snapshot);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch market snapshot", "message", e.getMessage()));
        }
    }

    @GetMapping("/indices")
    public ResponseEntity<?> getMarketIndices(@RequestParam(required = false, defaultValue = "HOSE") String exchange) {
        try {
            List<MarketIndexDTO> indices = marketService.getMarketIndices(exchange);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", indices,
                    "count", indices.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch market indices", "message", e.getMessage()));
        }
    }

    @GetMapping("/liquidity")
    public ResponseEntity<?> getMarketLiquidity(
            @RequestParam(required = false, defaultValue = "HOSE") String exchange) {
        try {
            MarketSnapshotDTO snapshot = stockService.getMarketOverview();
            LiquidityDTO liquidity = snapshot.getLiquidity();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "exchange", exchange,
                    "data", liquidity
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch liquidity data", "message", e.getMessage()));
        }
    }

    @GetMapping("/cashflow")
    public ResponseEntity<?> getMarketCashFlow(
            @RequestParam(required = false, defaultValue = "HOSE") String exchange) {
        try {
            MarketSnapshotDTO snapshot = stockService.getMarketOverview();
            CashFlowDTO cashFlow = snapshot.getCashFlow();
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "exchange", exchange,
                    "data", cashFlow
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch cash flow data", "message", e.getMessage()));
        }
    }
}
