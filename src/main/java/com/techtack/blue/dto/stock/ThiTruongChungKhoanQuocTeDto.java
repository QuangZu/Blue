package com.techtack.blue.dto.stock;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ThiTruongChungKhoanQuocTeDto {
    private String ngay;
    private String kieu_thoi_gian;
    private BigDecimal bsesn;
    private BigDecimal dji;
    private BigDecimal fchi;
    private BigDecimal ftse;
    private BigDecimal ftwisgpl;
    private BigDecimal gdaxi;
    private BigDecimal hsi;
    private BigDecimal ixic;
    private BigDecimal klse;
    private BigDecimal ks11;
    private BigDecimal n225;
    private BigDecimal psi;
    private BigDecimal seti;
    private BigDecimal spx;
    private BigDecimal ssec;
    private BigDecimal stoxx50;
    private BigDecimal vix;
}
