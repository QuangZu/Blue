package com.techtack.blue.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techtack.blue.dto.StockDto;
import com.techtack.blue.model.Stock;
import com.techtack.blue.repository.StockRepository;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;
    
    @Autowired
    private AlphaVantageService alphaVantageService;

    public StockDto getStockBySymbol(String symbol) {
        Stock stock = stockRepository.findBySymbol(symbol);
        
        if (stock == null) {
            stock = alphaVantageService.getStockQuote(symbol);
            if (stock != null) {
                stockRepository.save(stock);
            } else {
                return null;
            }
        }
        
        return convertToDto(stock);
    }

    public List<StockDto> getTopTradedStocks() {
        return convertToDtoList(stockRepository.findTopTradedStocks());
    }

    public List<StockDto> getTopGainers() {
        return convertToDtoList(stockRepository.findTopGainers());
    }

    public List<StockDto> getTopLosers() {
        return convertToDtoList(stockRepository.findTopLosers());
    }

    public List<StockDto> searchStocks(String query) {
        return convertToDtoList(alphaVantageService.searchStocks(query));
    }
    
    public List<StockDto> getStocksByIndustry(String industry) {
        return convertToDtoList(stockRepository.findByIndustry(industry));
    }
    
    public List<StockDto> getStocksByMarketCapRange(double minMarketCap, double maxMarketCap) {
        return convertToDtoList(stockRepository.findByMarketCapRange(minMarketCap, maxMarketCap));
    }

    public List<StockDto> getAllStocks() {
        return convertToDtoList(stockRepository.findAll());
    }

    private List<StockDto> convertToDtoList(List<Stock> stocks) {
        return stocks.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public StockDto convertToDto(Stock stock) {
        StockDto dto = new StockDto();
        dto.setId(stock.getId());
        dto.setSymbol(stock.getSymbol());
        dto.setName(stock.getName());
        dto.setPrice(stock.getPrice());
        dto.setOpen(stock.getOpen());
        dto.setHigh(stock.getHigh());
        dto.setLow(stock.getLow());
        dto.setPreviousClose(stock.getPreviousClose());
        dto.setVolume(stock.getVolume());
        dto.setLastUpdated(stock.getLastUpdated());
        dto.setIndustry(stock.getIndustry());
        dto.setMarketCap(stock.getMarketCap());
        
        double previousClose = stock.getPreviousClose();
        if (previousClose != 0) {
            double changeAmount = stock.getPrice() - previousClose;
            dto.setChangeAmount(changeAmount);
            dto.setChangePercent((changeAmount / previousClose) * 100);
        }
        
        return dto;
    }
}