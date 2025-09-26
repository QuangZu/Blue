package com.techtack.blue.dto.stock;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TyLeSoHuuNuocNgoaiDto {
    private List<Meta> meta;
    private List<Data> data;

    public static class Meta {
        private Long total_page;
        private Long total_count;
    }

    public static class Data {
        private String code;
        private Long id;
        private String time;
        private BigDecimal ratio_fo_max;
        private BigDecimal ratio_fo;
        private Long quantity_fo_max;
        private Long quantity_fo;
        private Long diff_fo;
        private String created_at;
        private String updated_at;
    }
}
