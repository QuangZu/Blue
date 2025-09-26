package com.techtack.blue.dto.stock;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CoDongLonDto {
    private String code;
    private List<Data> data;

    public static class Data{
        private String name;
        private Long share;
        private BigDecimal ratio;
        private String time;
        private List<Holding> holding;
        private List<History> history;

        public static class Holding{
            private String mack;
            private String name;
            private Long share;
            private BigDecimal ratio;
            private String time;
        }

        public static class History{
            private String time;
            private Long share;
            private BigDecimal ratio;
            private String source;
        }
    }
}
