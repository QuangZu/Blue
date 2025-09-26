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
public class ForeignTradingDTO {
    private String code;
    private String companyName;
    private BigDecimal foreignBuyVolume;
    private BigDecimal foreignBuyValue;
    private BigDecimal foreignSellVolume;
    private BigDecimal foreignSellValue;
    private BigDecimal foreignNetVolume;
    private BigDecimal foreignNetValue;
    private BigDecimal currentPrice;
    private BigDecimal changePercent;
}
