package com.techtack.blue.dto.stock;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BanLanhDaoDto {
    private String code;
    private List<Position> position;
    private Long share;
    private BigDecimal ratio;
    private BigDecimal value;
    private BigDecimal time;
    private List<Info> info;
    private List<Relationship> relationship;
    private String created_at;
    private String updated_at;

    public static class Position {
        private String position;
        private String company;
        private String company_code;
        private String ngaycongbo;
        private String last_update;
    }

    public static class Info {
        private String type;
        private String info;
    }

    public static class Relationship {
        private String name_relation;
        private String relationship;
        private String code;
        private Long share;
        private BigDecimal value;
        private String time;
    }
}
