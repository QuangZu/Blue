package com.techtack.blue.dto.stock;

import lombok.Data;

@Data
public class LichSuKienDto {
    private String code;
    private String ngay_thong_bao;
    private String ngay_dang_ky_cc;
    private String ngay_thuc_hien;
    private String type_event;
    private String title_event;
    private String event_url;
    private String event_doc;
    private String created_at;
    private String updated_at;
}
