package com.techtack.blue.dto.asset;

import lombok.Data;
import lombok.Builder;
import java.util.List;
import java.time.LocalDate;

@Data
@Builder
public class PortfolioPerformanceDTO {
    
    private String accountNumber;
    
    // Overview metrics
    private Double netAssetValue;
    private Double totalAssetValue;
    private Double dailyChange;
    private Double dailyChangePercent;
    
    // Profit and Loss periods
    private PnLPeriodDTO oneDay;
    private PnLPeriodDTO oneMonth;
    private PnLPeriodDTO oneYear;
    private PnLPeriodDTO yearToDate;
    
    // Performance chart data
    private List<PerformanceDataPoint> performanceHistory;
    private List<PerformanceDataPoint> benchmarkHistory; // VNINDEX
    private List<PerformanceDataPoint> ssiHistory; // SSI stock
    
    // Performance comparison
    private Double rateOfReturn;
    private Double benchmarkReturn;
    private Double alpha; // Excess return
    
    @Data
    @Builder
    public static class PnLPeriodDTO {
        private Double value;
        private Double percentage;
        private String period;
    }
    
    @Data
    @Builder
    public static class PerformanceDataPoint {
        private LocalDate date;
        private Double value;
        private Double percentChange;
        private String label; // "Rate of return", "VNINDEX", "SSI"
    }
}
