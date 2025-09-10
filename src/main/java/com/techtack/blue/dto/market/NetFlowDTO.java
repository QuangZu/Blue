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
public class NetFlowDTO {
    private BigDecimal foreignBuyValue;
    private BigDecimal foreignSellValue;
    private BigDecimal netValue;
    private LocalDateTime timestamp;
}
