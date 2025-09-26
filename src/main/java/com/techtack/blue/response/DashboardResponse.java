package com.techtack.blue.response;

import com.techtack.blue.dto.market.MarketIndexDTO;
import com.techtack.blue.dto.StockDto;
import lombok.Data;

import java.util.List;

@Data
public class DashboardResponse {
    private boolean status;
    private String message;
    private List<MarketIndexDTO> indices;
    private List<StockDto> activeStocks;
    private List<StockDto> topGainers;
    private List<StockDto> topLosers;
}