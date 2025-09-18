package com.techtack.blue.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.techtack.blue.config.AlphaVantageConfig;
import com.techtack.blue.dto.market.*;
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
    
    @Cacheable(value = "marketOverview", unless = "#result == null")
    public MarketSnapshotDTO getMarketOverview() {
        MarketSnapshotDTO snapshot = new MarketSnapshotDTO();
        snapshot.setExchange("Global");
        snapshot.setTimestamp(LocalDateTime.now());
        
        // Fetch major indices
        List<MarketIndexDTO> indices = getMajorIndices();
        snapshot.setIndices(indices);
        
        // Fetch top movers
        snapshot.setTopGainers(getTopGainers(10));
        snapshot.setTopLosers(getTopLosers(10));
        snapshot.setTopVolume(getMostActiveStocks(10));
        
        return snapshot;
    }
    
    @Cacheable(value = "marketIndices", unless = "#result == null || #result.isEmpty()")
    public List<MarketIndexDTO> getMajorIndices() {
        List<MarketIndexDTO> indices = new ArrayList<>();
        String[] indexSymbols = {"SPX", "DJI", "IXIC", "VIX"};
        String[] indexNames = {"S&P 500", "Dow Jones Industrial", "NASDAQ Composite", "Volatility Index"};
        
        for (int i = 0; i < indexSymbols.length; i++) {
            MarketIndexDTO index = getIndexQuote(indexSymbols[i], indexNames[i]);
            if (index != null) {
                indices.add(index);
            }
        }
        
        return indices;
    }
    
    private MarketIndexDTO getIndexQuote(String symbol, String name) {
        try {
            String url = config.getBaseUrl() + "?function=GLOBAL_QUOTE&symbol=" + symbol + "&apikey=" + config.getApiKey();
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response == null || !response.containsKey("Global Quote")) {
                return null;
            }
            
            Map<String, String> quote = (Map<String, String>) response.get("Global Quote");
            if (quote == null || quote.isEmpty()) {
                return null;
            }
            
            MarketIndexDTO index = new MarketIndexDTO();
            index.setIndexCode(symbol);
            index.setIndexName(name);
            index.setIndexValue(new BigDecimal(quote.getOrDefault("05. price", "0")));
            index.setPreviousClose(new BigDecimal(quote.getOrDefault("08. previous close", "0")));
            
            BigDecimal change = index.getIndexValue().subtract(index.getPreviousClose());
            index.setChangeValue(change);
            
            if (index.getPreviousClose().compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal changePercent = change.divide(index.getPreviousClose(), 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal(100));
                index.setChangePercent(changePercent);
            } else {
                index.setChangePercent(BigDecimal.ZERO);
            }
            
            index.setTotalVolume(new BigDecimal(quote.getOrDefault("06. volume", "0")));
            index.setTimestamp(LocalDateTime.now());
            
            return index;
        } catch (Exception e) {
            // Log error but don't throw to allow other indices to be fetched
            return null;
        }
    }
    
    @Cacheable(value = "topGainers", unless = "#result == null || #result.isEmpty()")
    public List<StockDataDTO> getTopGainers(int limit) {
        try {
            String url = config.getBaseUrl() + "?function=TOP_GAINERS_LOSERS&apikey=" + config.getApiKey();
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response == null || !response.containsKey("top_gainers")) {
                return Collections.emptyList();
            }
            
            List<Map<String, String>> gainers = (List<Map<String, String>>) response.get("top_gainers");
            return gainers.stream()
                .limit(limit)
                .map(this::convertToStockDataDTO)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
    
    @Cacheable(value = "topLosers", unless = "#result == null || #result.isEmpty()")
    public List<StockDataDTO> getTopLosers(int limit) {
        try {
            String url = config.getBaseUrl() + "?function=TOP_GAINERS_LOSERS&apikey=" + config.getApiKey();
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response == null || !response.containsKey("top_losers")) {
                return Collections.emptyList();
            }
            
            List<Map<String, String>> losers = (List<Map<String, String>>) response.get("top_losers");
            return losers.stream()
                .limit(limit)
                .map(this::convertToStockDataDTO)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
    
    @Cacheable(value = "mostActive", unless = "#result == null || #result.isEmpty()")
    public List<StockDataDTO> getMostActiveStocks(int limit) {
        try {
            String url = config.getBaseUrl() + "?function=TOP_GAINERS_LOSERS&apikey=" + config.getApiKey();
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response == null || !response.containsKey("most_actively_traded")) {
                return Collections.emptyList();
            }
            
            List<Map<String, String>> active = (List<Map<String, String>>) response.get("most_actively_traded");
            return active.stream()
                .limit(limit)
                .map(this::convertToStockDataDTO)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
    
    @Cacheable(value = "stockQuote", key = "#symbol", unless = "#result == null")
    public StockDataDTO getDetailedStockQuote(String symbol) {
        try {
            String url = config.getBaseUrl() + "?function=GLOBAL_QUOTE&symbol=" + symbol + "&apikey=" + config.getApiKey();
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response == null || !response.containsKey("Global Quote")) {
                return null;
            }
            
            Map<String, String> quote = (Map<String, String>) response.get("Global Quote");
            if (quote == null || quote.isEmpty()) {
                return null;
            }
            
            StockDataDTO stock = new StockDataDTO();
            stock.setSymbol(symbol);
            stock.setCurrentPrice(new BigDecimal(quote.getOrDefault("05. price", "0")));
            stock.setOpenPrice(new BigDecimal(quote.getOrDefault("02. open", "0")));
            stock.setHighPrice(new BigDecimal(quote.getOrDefault("03. high", "0")));
            stock.setLowPrice(new BigDecimal(quote.getOrDefault("04. low", "0")));
            stock.setReferencePrice(new BigDecimal(quote.getOrDefault("08. previous close", "0")));
            stock.setVolume(new BigDecimal(quote.getOrDefault("06. volume", "0")));
            
            BigDecimal change = stock.getCurrentPrice().subtract(stock.getReferencePrice());
            stock.setChangeAmount(change);
            
            if (stock.getReferencePrice().compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal changePercent = change.divide(stock.getReferencePrice(), 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal(100));
                stock.setChangePercent(changePercent);
            } else {
                stock.setChangePercent(BigDecimal.ZERO);
            }
            
            stock.setTimestamp(LocalDateTime.now());
            
            // Get company name from search
            String searchUrl = config.getBaseUrl() + "?function=SYMBOL_SEARCH&keywords=" + symbol + "&apikey=" + config.getApiKey();
            Map<String, Object> searchResponse = restTemplate.getForObject(searchUrl, Map.class);
            if (searchResponse != null && searchResponse.containsKey("bestMatches")) {
                List<Map<String, String>> matches = (List<Map<String, String>>) searchResponse.get("bestMatches");
                if (!matches.isEmpty()) {
                    stock.setCompanyName(matches.get(0).getOrDefault("2. name", symbol));
                }
            }
            
            return stock;
            
        } catch (Exception e) {
            return null;
        }
    }
    
    @Cacheable(value = "sectorPerformance", unless = "#result == null || #result.isEmpty()")
    public List<IndustryHeatmapDTO> getSectorPerformance() {
        try {
            String url = config.getBaseUrl() + "?function=SECTOR&apikey=" + config.getApiKey();
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response == null) {
                return Collections.emptyList();
            }
            
            List<IndustryHeatmapDTO> sectors = new ArrayList<>();
            Map<String, String> realtimePerf = (Map<String, String>) response.get("Rank A: Real-Time Performance");
            
            if (realtimePerf != null) {
                for (Map.Entry<String, String> entry : realtimePerf.entrySet()) {
                    IndustryHeatmapDTO sector = new IndustryHeatmapDTO();
                    sector.setIndustryName(entry.getKey());
                    sector.setIndustryCode(entry.getKey().toUpperCase().replace(" ", "_"));
                    
                    // Parse percentage (remove % sign)
                    String perfValue = entry.getValue().replace("%", "");
                    sector.setChangePercent(new BigDecimal(perfValue));
                    
                    sectors.add(sector);
                }
            }
            
            return sectors;
            
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
    
    private StockDataDTO convertToStockDataDTO(Map<String, String> data) {
        StockDataDTO dto = new StockDataDTO();
        dto.setSymbol(data.getOrDefault("ticker", ""));
        dto.setCurrentPrice(new BigDecimal(data.getOrDefault("price", "0")));
        dto.setVolume(new BigDecimal(data.getOrDefault("volume", "0")));
        
        // Handle change amount and percentage
        String changeAmount = data.getOrDefault("change_amount", "0");
        dto.setChangeAmount(new BigDecimal(changeAmount));
        
        String changePercentStr = data.getOrDefault("change_percentage", "0%");
        changePercentStr = changePercentStr.replace("%", "");
        dto.setChangePercent(new BigDecimal(changePercentStr));
        
        dto.setTimestamp(LocalDateTime.now());
        
        return dto;
    }
    
    @Cacheable(value = "intradayData", key = "#symbol + '_' + #interval", unless = "#result == null || #result.isEmpty()")
    public List<StockDataDTO> getIntradayData(String symbol, String interval) {
        try {
            String url = config.getBaseUrl() + "?function=TIME_SERIES_INTRADAY&symbol=" + symbol 
                + "&interval=" + interval + "&apikey=" + config.getApiKey();
            
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null) {
                return Collections.emptyList();
            }
            
            Map<String, Map<String, String>> timeSeries = (Map<String, Map<String, String>>) 
                response.get("Time Series (" + interval + ")");
                
            if (timeSeries == null) {
                return Collections.emptyList();
            }
            
            List<StockDataDTO> dataPoints = new ArrayList<>();
            for (Map.Entry<String, Map<String, String>> entry : timeSeries.entrySet()) {
                StockDataDTO point = new StockDataDTO();
                point.setSymbol(symbol);
                
                Map<String, String> values = entry.getValue();
                point.setOpenPrice(new BigDecimal(values.getOrDefault("1. open", "0")));
                point.setHighPrice(new BigDecimal(values.getOrDefault("2. high", "0")));
                point.setLowPrice(new BigDecimal(values.getOrDefault("3. low", "0")));
                point.setCurrentPrice(new BigDecimal(values.getOrDefault("4. close", "0")));
                point.setVolume(new BigDecimal(values.getOrDefault("5. volume", "0")));
                
                dataPoints.add(point);
            }
            
            return dataPoints;
            
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
