package com.techtack.blue.dto.market;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockDataDTO {
    private String code;
    private String companyName;
    private String exchange;
    private BigDecimal currentPrice;
    private BigDecimal referencePrice;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal changeAmount;
    private BigDecimal changePercent;
    private BigDecimal volume;
    private BigDecimal value;
    private BigDecimal marketCap;
    private LocalDateTime timestamp;
}
