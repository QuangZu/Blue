package com.techtack.blue.dto.stock;

import lombok.Data;

import java.util.List;

@Data
public class DanhSachNganhDto {
    private Long idnganhcap3;
    private String tennganhcap3;
    private String macks;
    private List<ListNganh> list;

    public static class ListNganh {
        private Long idnganhcap4;
        private String tennganhcap4;
        private String macks;
        private String created_at;
        private String updated_at;
    }
}
