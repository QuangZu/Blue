package com.techtack.blue.dto.stock;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ChiSoTaiChinhNganhDto {
    private Long idnganhcap3;
    private Long vonhoa;
    private Long soluongluuhanh;
    private BigDecimal eps;
    private BigDecimal pb;
    private BigDecimal pe;
    private BigDecimal smg;
    private BigDecimal dif;
    private BigDecimal dif_w;
    private BigDecimal dif_m;
    private BigDecimal dif_3m;
    private String created_at;
    private String updated_at;
}
