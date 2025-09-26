package com.techtack.blue.dto.stock;

import lombok.Data;

@Data
public class KeHoachKinhDoanhDto {
    private String code;
    private Long nam;
    private Long dt_kehoach;
    private Long tyle_hoanthanh_dt;
    private Long lntt_kehoach;
    private Long tyle_hoanthanh_lntt;
    private Long lnst_kehoach;
    private Long tyle_hoanthanh_lnst;
}
