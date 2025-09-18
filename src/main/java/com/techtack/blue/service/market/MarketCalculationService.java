package com.techtack.blue.service.market;

import com.techtack.blue.model.market.MarketData;
import com.techtack.blue.model.market.Symbol;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarketCalculationService {

    public BigDecimal calculateNetFlow(List<MarketData> marketDataList) {
        if (marketDataList == null || marketDataList.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return marketDataList.stream()
            .filter(data -> data.getForeignBuyValue() != null && data.getForeignSellValue() != null)
            .map(data -> data.getForeignBuyValue().subtract(data.getForeignSellValue()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateMarketLiquidity(List<MarketData> marketDataList) {
        if (marketDataList == null || marketDataList.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return marketDataList.stream()
            .map(MarketData::getValue)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateLiquidityRatio(BigDecimal currentLiquidity, BigDecimal averageLiquidity) {
        if (averageLiquidity == null || averageLiquidity.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentLiquidity.divide(averageLiquidity, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));
    }

    public BigDecimal calculateIndexContribution(Symbol symbol, BigDecimal priceChange) {
        if (symbol.getWeight() == null || priceChange == null) {
            return BigDecimal.ZERO;
        }
        return symbol.getWeight().multiply(priceChange).setScale(4, RoundingMode.HALF_UP);
    }

    public List<MarketData> findMajorImpactStocks(List<MarketData> marketDataList, int limit) {
        if (marketDataList == null || marketDataList.isEmpty()) {
            return new ArrayList<>();
        }
        return marketDataList.stream()
            .filter(data -> data.getSymbol() != null && data.getSymbol().getWeight() != null)
            .sorted((a, b) -> {
                BigDecimal impactA = calculateIndexContribution(a.getSymbol(), a.getChangePercent());
                BigDecimal impactB = calculateIndexContribution(b.getSymbol(), b.getChangePercent());
                return impactB.abs().compareTo(impactA.abs());
            })
            .limit(limit)
            .collect(Collectors.toList());
    }

    public Map<String, Integer> calculateMarketBreadth(List<MarketData> marketDataList) {
        Map<String, Integer> breadth = new HashMap<>();
        
        if (marketDataList == null || marketDataList.isEmpty()) {
            breadth.put("advances", 0);
            breadth.put("declines", 0);
            breadth.put("unchanged", 0);
            return breadth;
        }
        
        int advances = (int) marketDataList.stream()
            .filter(data -> data.getChangePercent() != null && data.getChangePercent().compareTo(BigDecimal.ZERO) > 0)
            .count();
            
        int declines = (int) marketDataList.stream()
            .filter(data -> data.getChangePercent() != null && data.getChangePercent().compareTo(BigDecimal.ZERO) < 0)
            .count();
            
        int unchanged = (int) marketDataList.stream()
            .filter(data -> data.getChangePercent() != null && data.getChangePercent().compareTo(BigDecimal.ZERO) == 0)
            .count();
            
        breadth.put("advances", advances);
        breadth.put("declines", declines);
        breadth.put("unchanged", unchanged);
        
        return breadth;
    }

    public BigDecimal calculatePriceWeightedIndex(List<MarketData> marketDataList) {
        if (marketDataList.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal totalPrice = marketDataList.stream()
            .map(MarketData::getCurrentPrice)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        return totalPrice.divide(BigDecimal.valueOf(marketDataList.size()), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateMarketCapWeightedIndex(List<MarketData> marketDataList, BigDecimal basePeriodValue) {
        BigDecimal currentMarketCap = marketDataList.stream()
            .filter(data -> data.getSymbol() != null && data.getSymbol().getMarketCap() != null)
            .map(data -> {
                BigDecimal marketCap = data.getSymbol().getMarketCap();
                BigDecimal priceRatio = data.getCurrentPrice()
                    .divide(data.getReferencePrice(), 4, RoundingMode.HALF_UP);
                return marketCap.multiply(priceRatio);
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        BigDecimal baseMarketCap = marketDataList.stream()
            .filter(data -> data.getSymbol() != null && data.getSymbol().getMarketCap() != null)
            .map(data -> data.getSymbol().getMarketCap())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        if (baseMarketCap.compareTo(BigDecimal.ZERO) == 0) {
            return basePeriodValue;
        }
        
        return basePeriodValue.multiply(currentMarketCap)
            .divide(baseMarketCap, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateSectorPerformance(List<MarketData> sectorStocks) {
        if (sectorStocks == null || sectorStocks.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal totalWeightedChange = sectorStocks.stream()
            .filter(data -> data.getSymbol() != null && data.getSymbol().getMarketCap() != null)
            .map(data -> {
                BigDecimal weight = data.getSymbol().getMarketCap();
                BigDecimal change = data.getChangePercent() != null ? data.getChangePercent() : BigDecimal.ZERO;
                return weight.multiply(change);
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        BigDecimal totalMarketCap = sectorStocks.stream()
            .filter(data -> data.getSymbol() != null && data.getSymbol().getMarketCap() != null)
            .map(data -> data.getSymbol().getMarketCap())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        if (totalMarketCap.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return totalWeightedChange.divide(totalMarketCap, 2, RoundingMode.HALF_UP);
    }

    public Map<String, BigDecimal> calculateMarketCashFlow(List<MarketData> marketDataList) {
        Map<String, BigDecimal> cashFlow = new HashMap<>();
        
        if (marketDataList == null || marketDataList.isEmpty()) {
            cashFlow.put("inflow", BigDecimal.ZERO);
            cashFlow.put("outflow", BigDecimal.ZERO);
            cashFlow.put("netFlow", BigDecimal.ZERO);
            return cashFlow;
        }
        
        BigDecimal totalInflow = marketDataList.stream()
            .filter(data -> data.getChangePercent() != null && data.getChangePercent().compareTo(BigDecimal.ZERO) > 0)
            .map(MarketData::getValue)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        BigDecimal totalOutflow = marketDataList.stream()
            .filter(data -> data.getChangePercent() != null && data.getChangePercent().compareTo(BigDecimal.ZERO) < 0)
            .map(MarketData::getValue)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        cashFlow.put("inflow", totalInflow);
        cashFlow.put("outflow", totalOutflow);
        cashFlow.put("netFlow", totalInflow.subtract(totalOutflow));
        
        return cashFlow;
    }

    public BigDecimal calculateVWAP(List<MarketData> marketDataList) {
        BigDecimal totalValue = marketDataList.stream()
            .map(data -> {
                if (data.getCurrentPrice() != null && data.getVolume() != null) {
                    return data.getCurrentPrice().multiply(data.getVolume());
                }
                return BigDecimal.ZERO;
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        BigDecimal totalVolume = marketDataList.stream()
            .map(MarketData::getVolume)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        if (totalVolume.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return totalValue.divide(totalVolume, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateMarketRSI(List<MarketData> marketDataList, int period) {
        if (marketDataList.size() < period) {
            return BigDecimal.valueOf(50); // Neutral RSI
        }
        
        BigDecimal avgGain = BigDecimal.ZERO;
        BigDecimal avgLoss = BigDecimal.ZERO;
        int gainCount = 0;
        int lossCount = 0;
        
        for (MarketData data : marketDataList) {
            if (data.getChangeAmount() != null) {
                if (data.getChangeAmount().compareTo(BigDecimal.ZERO) > 0) {
                    avgGain = avgGain.add(data.getChangeAmount());
                    gainCount++;
                } else if (data.getChangeAmount().compareTo(BigDecimal.ZERO) < 0) {
                    avgLoss = avgLoss.add(data.getChangeAmount().abs());
                    lossCount++;
                }
            }
        }
        
        if (gainCount > 0) {
            avgGain = avgGain.divide(BigDecimal.valueOf(gainCount), 4, RoundingMode.HALF_UP);
        }
        
        if (lossCount > 0) {
            avgLoss = avgLoss.divide(BigDecimal.valueOf(lossCount), 4, RoundingMode.HALF_UP);
        }
        
        if (avgLoss.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.valueOf(100); // Maximum RSI
        }
        
        BigDecimal rs = avgGain.divide(avgLoss, 4, RoundingMode.HALF_UP);
        BigDecimal rsi = BigDecimal.valueOf(100).subtract(
            BigDecimal.valueOf(100).divide(BigDecimal.ONE.add(rs), 2, RoundingMode.HALF_UP)
        );
        
        return rsi;
    }
}
