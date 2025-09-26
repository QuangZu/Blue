package com.techtack.blue.dto.stock;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ThiTruongChungKhoanVietNamDto {
    private String kieu_thoi_gian;
    private String ngay;
    private BigDecimal vnindex;
    private BigDecimal hnxindex;
    private BigDecimal upindex;
    private BigDecimal vn30;
    private BigDecimal hnx30;
}
