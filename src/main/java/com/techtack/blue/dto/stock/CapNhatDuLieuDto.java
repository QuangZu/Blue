package com.techtack.blue.dto.stock;

import lombok.Data;

import java.util.List;

@Data
public class CapNhatDuLieuDto {
    private List<Meta> meta;
    private List<Data> data;

    public static class Meta {
        private Long total_page;
        private Long total_count;
    }

    public static class Data {
        private Long id;
        private String iud_action;
        private Long wifeed_api_id;
        private List<UpdateInfo> update_info;
        private String created_at;
        private String url_demo;
        private Long name;

        public static class UpdateInfo {
            private String code;
            private String year;
            private Long quarter;
        }
    }
}
