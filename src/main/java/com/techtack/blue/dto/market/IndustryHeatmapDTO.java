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
public class IndustryHeatmapDTO {
    private String industryCode;
    private String industryName;
    private String industryNameEn;
    private BigDecimal changePercent;
    private BigDecimal totalVolume;
    private BigDecimal totalValue;
    private BigDecimal marketCap;
    private Integer stockCount;
    private Integer advances;
    private Integer declines;
    private Integer unchanged;
}
