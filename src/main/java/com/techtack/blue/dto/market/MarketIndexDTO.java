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
public class MarketIndexDTO {
    private String indexCode;
    private String indexName;
    private String exchange;
    private BigDecimal indexValue;
    private BigDecimal previousClose;
    private BigDecimal changeValue;
    private BigDecimal changePercent;
    private BigDecimal totalVolume;
    private BigDecimal totalValue;
    private Integer advances;
    private Integer declines;
    private Integer unchanged;
    private LocalDateTime timestamp;
}
