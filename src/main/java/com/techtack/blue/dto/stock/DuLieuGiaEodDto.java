package com.techtack.blue.dto.stock;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DuLieuGiaEodDto {
    private String mack;
    private String ngay;
    private BigDecimal open_root;
    private BigDecimal high_root;
    private BigDecimal low_root;
    private BigDecimal close_root;
    private BigDecimal volume_root;
    private BigDecimal open_adjust;
    private BigDecimal high_adjust;
    private BigDecimal low_adjust;
    private BigDecimal close_adjust;
    private BigDecimal volume_adjust;
    private BigDecimal avgprice;
    private BigDecimal giatri_giaodich;
    private BigDecimal ceilingprice;
    private BigDecimal floorprice;
    private BigDecimal changed;
    private BigDecimal changedratio;
    private BigDecimal kl_nn_ban;
    private BigDecimal kl_nn_mua;
    private BigDecimal gt_nn_ban;
    private BigDecimal gt_nn_mua;
    private String lastupdate;
}
