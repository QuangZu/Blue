package com.techtack.blue.controller;

import com.techtack.blue.dto.MarketDto;
import com.techtack.blue.dto.market.*;
import com.techtack.blue.service.MarketService;
import com.techtack.blue.service.market.MarketSnapshotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/market")
public class MarketController {

    @Autowired
    private MarketService marketService;
    
    @Autowired
    private MarketSnapshotService marketSnapshotService;

    @GetMapping("/snapshot")
    public ResponseEntity<?> getMarketSnapshot(
            @RequestParam(required = false, defaultValue = "HOSE") String exchange) {
        try {
            MarketSnapshotDTO snapshot = marketSnapshotService.getMarketSnapshot(exchange);
            return ResponseEntity.ok(snapshot);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch market snapshot", "message", e.getMessage()));
        }
    }

    @GetMapping("/indices")
    public ResponseEntity<?> getMarketIndices(@RequestParam(required = false) String exchange) {
        try {
            List<MarketIndexDTO> indices = marketSnapshotService.getMarketIndices(exchange);
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

    @GetMapping("/indices/{code}")
    public ResponseEntity<?> getMarketIndexByCode(@PathVariable String code) {
        try {
            MarketDto index = marketService.getMarketIndexByCode(code);
            if (index == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Index not found", "code", code));
            }
            return ResponseEntity.ok(Map.of("success", true, "data", index));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch market index", "message", e.getMessage()));
        }
    }

    /**
     * Get market net flow
     */
    @GetMapping("/netflow")
    public ResponseEntity<?> getMarketNetFlow(
            @RequestParam(required = false, defaultValue = "HOSE") String marketCode) {
        try {
            List<MarketDto> netFlows = marketService.getMarketNetFlow(marketCode);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "market", marketCode,
                "data", netFlows
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch market net flow", "message", e.getMessage()));
        }
    }

    @GetMapping("/heatmap")
    public ResponseEntity<?> getIndustryHeatmap(
            @RequestParam(required = false, defaultValue = "HOSE") String exchange) {
        try {
            List<IndustryHeatmapDTO> heatmap = marketSnapshotService.getIndustryHeatmap(exchange);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "exchange", exchange,
                "data", heatmap,
                "count", heatmap.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch heatmap data", "message", e.getMessage()));
        }
    }

    @GetMapping("/movers")
    public ResponseEntity<?> getMarketMovers(
            @RequestParam(required = false, defaultValue = "HOSE") String marketCode,
            @RequestParam(required = false, defaultValue = "TOP_GAINER") String moverType) {
        try {
            List<MarketDto> movers = marketService.getMarketMovers(marketCode, moverType);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "market", marketCode,
                "type", moverType,
                "data", movers
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid mover type", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch market movers", "message", e.getMessage()));
        }
    }

    @GetMapping("/top-gainers")
    public ResponseEntity<?> getTopGainers(
            @RequestParam(required = false, defaultValue = "HOSE") String exchange,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        try {
            var marketData = marketSnapshotService.getLatestMarketData(exchange);
            List<StockDataDTO> topGainers = marketSnapshotService.getTopGainers(marketData, limit);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "exchange", exchange,
                "data", topGainers
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch top gainers", "message", e.getMessage()));
        }
    }

    @GetMapping("/top-losers")
    public ResponseEntity<?> getTopLosers(
            @RequestParam(required = false, defaultValue = "HOSE") String exchange,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        try {
            var marketData = marketSnapshotService.getLatestMarketData(exchange);
            List<StockDataDTO> topLosers = marketSnapshotService.getTopLosers(marketData, limit);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "exchange", exchange,
                "data", topLosers
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch top losers", "message", e.getMessage()));
        }
    }

    @GetMapping("/top-volume")
    public ResponseEntity<?> getTopVolume(
            @RequestParam(required = false, defaultValue = "HOSE") String exchange,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        try {
            var marketData = marketSnapshotService.getLatestMarketData(exchange);
            List<StockDataDTO> topVolume = marketSnapshotService.getTopVolume(marketData, limit);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "exchange", exchange,
                "data", topVolume
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch top volume stocks", "message", e.getMessage()));
        }
    }

    @GetMapping("/foreign-trading")
    public ResponseEntity<?> getForeignTrading(
            @RequestParam(required = false, defaultValue = "HOSE") String exchange,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        try {
            var marketData = marketSnapshotService.getLatestMarketData(exchange);
            List<ForeignTradingDTO> foreignTrading = marketSnapshotService.getTopForeignTrading(marketData, limit);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "exchange", exchange,
                "data", foreignTrading
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch foreign trading data", "message", e.getMessage()));
        }
    }

    @GetMapping("/liquidity")
    public ResponseEntity<?> getMarketLiquidity(
            @RequestParam(required = false, defaultValue = "HOSE") String exchange) {
        try {
            var marketData = marketSnapshotService.getLatestMarketData(exchange);
            LiquidityDTO liquidity = marketSnapshotService.getMarketLiquidity(marketData);
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
            var marketData = marketSnapshotService.getLatestMarketData(exchange);
            CashFlowDTO cashFlow = marketSnapshotService.getMarketCashFlow(marketData);
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

    @GetMapping("/impact-stocks")
    public ResponseEntity<?> getMajorImpactStocks(
            @RequestParam(required = false, defaultValue = "HOSE") String exchange,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        try {
            var marketData = marketSnapshotService.getLatestMarketData(exchange);
            List<StockImpactDTO> impactStocks = marketSnapshotService.getMajorImpactStocks(marketData, limit);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "exchange", exchange,
                "data", impactStocks
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch impact stocks", "message", e.getMessage()));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getMarketStatistics(
            @RequestParam(required = false, defaultValue = "HOSE") String exchange) {
        try {
            var marketData = marketSnapshotService.getLatestMarketData(exchange);
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("exchange", exchange);
            stats.put("totalStocks", marketData.size());
            
            // Calculate market breadth
            int advances = 0, declines = 0, unchanged = 0;
            for (var data : marketData) {
                if (data.getChangePercent() != null) {
                    var change = data.getChangePercent().doubleValue();
                    if (change > 0) advances++;
                    else if (change < 0) declines++;
                    else unchanged++;
                }
            }
            
            stats.put("advances", advances);
            stats.put("declines", declines);
            stats.put("unchanged", unchanged);
            stats.put("advanceDeclineRatio", declines > 0 ? (double) advances / declines : advances);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", stats
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch market statistics", "message", e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "Market API",
            "timestamp", System.currentTimeMillis()
        ));
    }
}
