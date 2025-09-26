package com.techtack.blue.dto.stock;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GiaoDichNoiBoDto {
    private List<Meta> meta;
    private List<Data> data;

    public static class Meta {
        private Long total_page;
        private Long total_count;
    }

    public static class Data {
        private String code;
        private String type;
        private String name;
        private String position;
        private String relationship_name;
        private String relationship_position;
        private Long share_before;
        private Long amount_reg;
        private Long start_reg;
        private String end_reg;
        private Long amount_result;
        private String date_result;
        private Long share_after;
        private BigDecimal ratio;
        private String note;
    }
}
