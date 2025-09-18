package com.techtack.blue.dto.asset;

import lombok.Data;
import lombok.Builder;
import java.util.List;

@Data
@Builder
public class PortfolioDTO {
    
    private String accountNumber;
    private String accountName;
    private Double totalValue;
    private Double totalNAV;
    private Double totalCost;
    private Double profitLoss;
    private Double profitLossPercent;
    
    // Portfolio holdings
    private List<PortfolioHoldingDTO> holdings;
    
    // Portfolio allocation (for allocation tab)
    private List<AllocationDTO> allocations;
    
    @Data
    @Builder
    public static class PortfolioHoldingDTO {
        private String symbol;
        private String companyName;
        private Integer totalQuantity;
        private Integer sellableQuantity;
        private Double marketPrice;
        private Double averagePrice;
        private Double totalValue;
        private Double profitLoss;
        private Double profitLossPercent;
        private Double allocation; // % of portfolio
    }
    
    @Data
    @Builder
    public static class AllocationDTO {
        private String category; // "Stock", "Bond", "Fund", "Cash"
        private Double value;
        private Double percentage;
        private String color; // for chart display
    }
}
