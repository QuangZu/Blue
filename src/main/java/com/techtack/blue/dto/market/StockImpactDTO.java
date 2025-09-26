package com.techtack.blue.dto.market;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockImpactDTO {
    private String code;
    private String companyName;
    private BigDecimal currentPrice;
    private BigDecimal changePercent;
    private BigDecimal volume;
    private BigDecimal value;
    private BigDecimal weight;
    private BigDecimal contribution;
}
