package com.techtack.blue.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StockDto {
    private Long id;
    private String code;
    private String name;
    private String exchange;

    private Double price;
    private Double previousClose;
    private Long volume;
    private Double marketCap;

    private Double changeAmount;
    private Double changePercent;

    private LocalDateTime lastUpdated;

    private String fullname_vi;
    private String loaidn;
    private String san;
}
