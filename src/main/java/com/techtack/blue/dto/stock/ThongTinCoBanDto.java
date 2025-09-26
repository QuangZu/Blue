package com.techtack.blue.dto.stock;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ThongTinCoBanDto {
    @JsonProperty("mack")
    private String mack;
    
    @JsonProperty("ten")
    private String ten;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("loai_hinh_cong_ty")
    private String loai_hinh_cong_ty;
    
    @JsonProperty("san_niem_yet")
    private String san_niem_yet;
    
    @JsonProperty("gioithieu")
    private String gioithieu;
    
    @JsonProperty("donvikiemtoan")
    private List<DonViKiemToanDto> donvikiemtoan;
    
    @JsonProperty("ghichu")
    private String ghichu;
    
    @JsonProperty("diachi")
    private String diachi;
    
    @JsonProperty("website")
    private String website;
    
    @JsonProperty("nganhcap1")
    private String nganhcap1;
    
    @JsonProperty("nganhcap2")
    private String nganhcap2;
    
    @JsonProperty("nganhcap3")
    private String nganhcap3;
    
    @JsonProperty("nganhcap4")
    private String nganhcap4;
    
    @JsonProperty("ngayniemyet")
    private String ngayniemyet;
    
    @JsonProperty("smg")
    private BigDecimal smg;
    
    @JsonProperty("volume_daily")
    private Long volume_daily;
    
    @JsonProperty("vol_tb_15ngay")
    private BigDecimal vol_tb_15ngay;
    
    @JsonProperty("vonhoa")
    private Long vonhoa;
    
    @JsonProperty("dif")
    private Long dif;
    
    @JsonProperty("dif_percent")
    private BigDecimal dif_percent;
    
    @JsonProperty("tong_tai_san")
    private Long tong_tai_san;
    
    @JsonProperty("soluongluuhanh")
    private Long soluongluuhanh;
    
    @JsonProperty("soluongniemyet")
    private Long soluongniemyet;
    
    @JsonProperty("cophieuquy")
    private Long cophieuquy;
    
    @JsonProperty("logo")
    private String logo;
    
    @JsonProperty("created_at")
    private String created_at;
    
    @JsonProperty("updated_at")
    private String updated_at;
}
