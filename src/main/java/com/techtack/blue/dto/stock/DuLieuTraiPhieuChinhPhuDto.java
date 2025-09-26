package com.techtack.blue.dto.stock;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DuLieuTraiPhieuChinhPhuDto {
    private String ngay;
    private BigDecimal vietnam_1y;
    private BigDecimal vietnam_3y;
    private BigDecimal vietnam_5y;
    private BigDecimal vietnam_10y;
    private BigDecimal vietnam_15y;
    private BigDecimal vietnam_20y;
    private BigDecimal us_1y;
    private BigDecimal us_3y;
    private BigDecimal us_5y;
    private BigDecimal us_10y;
    private BigDecimal us_15y;
    private BigDecimal us_20y;
    private BigDecimal china_1y;
    private BigDecimal china_3y;
    private BigDecimal china_5y;
    private BigDecimal china_10y;
    private BigDecimal china_15y;
    private BigDecimal china_20y;
}
