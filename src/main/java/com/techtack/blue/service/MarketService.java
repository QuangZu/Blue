package com.techtack.blue.service;

import com.techtack.blue.dto.*;
import com.techtack.blue.model.*;
import com.techtack.blue.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MarketService {

    @Autowired
    private MarketRepository marketRepository;
    
    public List<MarketDto> getAllMarketIndices() {
        List<Market> indices = marketRepository.findByType(Market.MarketType.INDEX);
        return indices.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    public MarketDto getMarketIndexByCode(String code) {
        Market index = marketRepository.findByCodeAndType(code, Market.MarketType.INDEX);
        if (index == null) {
            return null;
        }
        return convertToDto(index);
    }
    
    // Market Net Flow methods
    public List<MarketDto> getMarketNetFlow(String marketCode) {
        Market parentMarket = marketRepository.findByCodeAndType(marketCode, Market.MarketType.INDEX);
        if (parentMarket == null) {
            return List.of();
        }
        
        List<Market> netFlows = marketRepository.findByParentMarketAndType(parentMarket, Market.MarketType.NET_FLOW);
        return netFlows.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    // Industry Heatmap methods
    public List<MarketDto> getIndustryHeatmap(String marketCode) {
        Market parentMarket = marketRepository.findByCodeAndType(marketCode, Market.MarketType.INDEX);
        if (parentMarket == null) {
            return List.of();
        }
        
        List<Market> heatmaps = marketRepository.findByParentMarketAndType(parentMarket, Market.MarketType.INDUSTRY_HEATMAP);
        return heatmaps.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    // Market Movers methods
    public List<MarketDto> getMarketMovers(String marketCode, String moverType) {
        Market parentMarket = marketRepository.findByCodeAndType(marketCode, Market.MarketType.INDEX);
        if (parentMarket == null) {
            return List.of();
        }
        
        Market.MarketCategory category = Market.MarketCategory.valueOf(moverType.toUpperCase());
        List<Market> movers = marketRepository.findByParentMarketAndTypeAndCategoryOrderByRankAsc(
            parentMarket, Market.MarketType.MARKET_MOVER, category);
        return movers.stream().map(this::convertToDto).collect(Collectors.toList());
    }
    
    // Unified conversion method
    private MarketDto convertToDto(Market market) {
        MarketDto dto = new MarketDto();
        dto.setId(market.getId());
        dto.setCode(market.getCode());
        dto.setType(market.getType() != null ? market.getType().name() : null);
        dto.setCategory(market.getCategory() != null ? market.getCategory().name() : null);
        
        // Basic market data
        dto.setName(market.getName());
        dto.setValue(market.getValue());
        dto.setChange(market.getChange());
        dto.setChangePercent(market.getChangePercent());
        dto.setVolume(market.getVolume());
        dto.setLastUpdated(market.getLastUpdated());
        
        // Extended attributes
        dto.setNetFlow(market.getNetFlow());
        dto.setCashIn(market.getCashIn());
        dto.setCashOut(market.getCashOut());
        dto.setDate(market.getDate());
        
        // Industry/Sector specific
        dto.setIndustryName(market.getIndustryName());
        dto.setMarketCap(market.getMarketCap());
        dto.setNumberOfStocks(market.getNumberOfStocks());
        
        // Impact and ranking data
        dto.setImpactValue(market.getImpactValue());
        dto.setImpactPercentage(market.getImpactPercentage());
        dto.setRank(market.getRank());
        
        // Foreign trading data
        dto.setBuyValue(market.getBuyValue());
        dto.setSellValue(market.getSellValue());
        dto.setNetValue(market.getNetValue());
        
        // Liquidity data
        dto.setLiquidityValue(market.getLiquidityValue());
        dto.setNormalizedValue(market.getNormalizedValue());
        
        // Relationships
        dto.setParentMarketId(market.getParentMarket() != null ? market.getParentMarket().getId() : null);
        dto.setRelatedStockId(market.getRelatedStock() != null ? market.getRelatedStock().getId() : null);
        
        // Additional data
        dto.setAdditionalData(market.getAdditionalData());
        
        return dto;
    }
}