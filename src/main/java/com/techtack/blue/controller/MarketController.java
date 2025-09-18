package com.techtack.blue.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techtack.blue.dto.MarketDto;
import com.techtack.blue.dto.market.CashFlowDTO;
import com.techtack.blue.dto.market.IndustryHeatmapDTO;
import com.techtack.blue.dto.market.LiquidityDTO;
import com.techtack.blue.dto.market.MarketIndexDTO;
import com.techtack.blue.dto.market.MarketSnapshotDTO;
import com.techtack.blue.dto.market.StockDataDTO;
import com.techtack.blue.dto.market.StockImpactDTO;
import com.techtack.blue.service.MarketService;

@RestController
@RequestMapping("/market")
public class MarketController {
    
    @Autowired
    private MarketService marketService;

    @GetMapping("/snapshot")
    public ResponseEntity<?> getMarketSnapshot(@RequestParam(required = false, defaultValue = "HOSE") String exchange){
        try {
            MarketSnapshotDTO snapshot = marketService.getCompleteMarketSnapshot(exchange);
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
            List<IndustryHeatmapDTO> heatmap = marketService.getIndustryHeatmap(exchange);
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
            List<StockDataDTO> topGainers = marketService.getTopGainers(exchange, limit);
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
            List<StockDataDTO> topLosers = marketService.getTopLosers(exchange, limit);
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
            List<StockDataDTO> topVolume = marketService.getTopVolume(exchange, limit);
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
            // Foreign trading is simulated in the unified service as part of net flow
            List<MarketDto> netFlow = marketService.getMarketNetFlow(exchange);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "exchange", exchange,
                "data", netFlow.subList(0, Math.min(limit, netFlow.size()))
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
            MarketSnapshotDTO snapshot = marketService.getCompleteMarketSnapshot(exchange);
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
            MarketSnapshotDTO snapshot = marketService.getCompleteMarketSnapshot(exchange);
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

    @GetMapping("/impact-stocks")
    public ResponseEntity<?> getMajorImpactStocks(
            @RequestParam(required = false, defaultValue = "HOSE") String exchange,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        try {
            MarketSnapshotDTO snapshot = marketService.getCompleteMarketSnapshot(exchange);
            List<StockImpactDTO> impactStocks = snapshot.getMajorImpactStocks();
            if (impactStocks != null && impactStocks.size() > limit) {
                impactStocks = impactStocks.subList(0, limit);
            }
            return ResponseEntity.ok(Map.of(
                "success", true,
                "exchange", exchange,
                "data", impactStocks != null ? impactStocks : Collections.emptyList()
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
            Map<String, Object> stats = marketService.getMarketStatistics(exchange);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", stats
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch market statistics", "message", e.getMessage()));
        }
    }
}
