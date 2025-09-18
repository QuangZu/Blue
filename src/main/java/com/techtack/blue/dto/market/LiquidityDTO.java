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
public class LiquidityDTO {
    private BigDecimal currentLiquidity;
    private BigDecimal averageLiquidity;
    private BigDecimal liquidityRatio;
    private LocalDateTime timestamp;
}
