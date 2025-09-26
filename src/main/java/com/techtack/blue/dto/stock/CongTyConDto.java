package com.techtack.blue.dto.stock;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CongTyConDto {
    private String code;
    private String company;
    private String code_subcompany;
    private BigDecimal ty_le_so_huu;
    private String type_company_name;
    private String type_company_owne;
    private Long von_dieu_le;
}
