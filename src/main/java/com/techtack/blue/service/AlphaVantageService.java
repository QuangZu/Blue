package com.techtack.blue.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.techtack.blue.config.AlphaVantageConfig;
import com.techtack.blue.model.Stock;

@Service
public class AlphaVantageService {

    private final RestTemplate restTemplate;
    private final AlphaVantageConfig config;

    @Autowired
    public AlphaVantageService(RestTemplate restTemplate, AlphaVantageConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    public Stock getStockQuote(String symbol) {
        try {
            String url = config.getBaseUrl() + "?function=GLOBAL_QUOTE&symbol=" + symbol + "&apikey=" + config.getApiKey();
            
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Alpha Vantage API returned null response");
            }
            
            Map<String, String> globalQuote = (Map<String, String>) response.get("Global Quote");
            if (globalQuote == null || globalQuote.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock not found: " + symbol);
            }
            
            return createStockFromQuote(symbol, globalQuote);
            
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Failed to fetch stock quote: " + e.getMessage());
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid stock data format from API");
        }
    }

    public List<Stock> searchStocks(String keywords) {
        try {
            String url = config.getBaseUrl() + "?function=SYMBOL_SEARCH&keywords=" + keywords + "&apikey=" + config.getApiKey();
            
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null) {
                return Collections.emptyList();
            }
            
            List<Map<String, String>> matches = (List<Map<String, String>>) response.get("bestMatches");
            if (matches == null || matches.isEmpty()) {
                return Collections.emptyList();
            }
            
            return matches.stream()
                    .map(this::createStockFromSearch)
                    .collect(Collectors.toList());
                    
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Failed to search stocks: " + e.getMessage());
        }
    }
    
    private Stock createStockFromQuote(String symbol, Map<String, String> globalQuote) {
        Stock stock = new Stock();
        stock.setSymbol(symbol);
        stock.setPrice(parseDouble(globalQuote.get("05. price")));
        stock.setOpen(parseDouble(globalQuote.get("02. open")));
        stock.setHigh(parseDouble(globalQuote.get("03. high")));
        stock.setLow(parseDouble(globalQuote.get("04. low")));
        stock.setPreviousClose(parseDouble(globalQuote.get("08. previous close")));
        stock.setVolume(parseLong(globalQuote.get("06. volume")));
        stock.setLastUpdated(LocalDateTime.now());
        return stock;
    }
    
    private Stock createStockFromSearch(Map<String, String> match) {
        Stock stock = new Stock();
        stock.setSymbol(match.get("1. symbol"));
        stock.setName(match.get("2. name"));
        stock.setPrice(parseDouble(match.getOrDefault("9. matchScore", "0")));
        stock.setLastUpdated(LocalDateTime.now());
        return stock;
    }
    
    private Double parseDouble(String value) {
        try {
            return value != null ? Double.parseDouble(value) : 0.0;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    private Long parseLong(String value) {
        try {
            return value != null ? Long.parseLong(value) : 0L;
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}