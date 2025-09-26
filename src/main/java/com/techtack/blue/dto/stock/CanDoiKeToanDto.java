package com.techtack.blue.dto.stock;

import lombok.Data;

@Data
public class CanDoiKeToanDto {
    private String code;
    private Long quy;
    private Long nam;

    // A. TỔNG TÀI SẢN
    private Long tongtaisan;
    private Long tienmatvangbacdaquy;
    private Long tienguitainganhangnhanuoc;
    private Long tienguivachovaycactctdkhac;
    private Long tienvangguitaitctdkhac;
    private Long chovaycactctdkhac;
    private Long duphongruirochovaycactctdkhac;
    private Long chungkhoankinhdoanhrong;
    private Long chungkhoankinhdoanh;
    private Long duphonggiamgiachungkhoankinhdoanh;
    private Long caccongcutaichinhphaisinhvacactaisantaichinhkhac;
    private Long chovaykhachhangrong;
    private Long chovaykhachhang;
    private Long duphongruirochovaykhachhang;
    private Long chungkhoandautu;
    private Long chungkhoandautusansangdeban;
    private Long chungkhoandautugiudenngaydaohan;
    private Long duphonggiamgiachungkhoandautu;
    private Long gopvondautudaihan;
    private Long dautuvaocongtycon;
    private Long dautuvaocongtyliendoanhlienket;
    private Long dautudaihankhac;
    private Long duphonggiamgiadautudaihan;

    // IX. Tài sản cố định
    private Long taisancodinh;
    private Long taisancodinhhuuhinh;
    private Long nguyengia_tscdhh;
    private Long ciatrihaomonluyke_tscdhh;
    private Long taisancodinhthuetaichinh;
    private Long nguyengia_tscdttc;
    private Long ciatrihaomonluyke_tscdttc;
    private Long taisancodinhvohinh;
    private Long nguyengia_tscdvh;
    private Long ciatrihaomonluyke_tscdvh;

    // X. Bất động sản đầu tư
    private Long batdongsandautu;
    private Long nguyengia_bdsdt;
    private Long ciatrihaomonluyke_bdsdt;

    // XI. Tài sản có khác
    private Long taisancokhac;
    private Long cackhoanphaithu;
    private Long cackhoanlaiphiphaithu;
    private Long taisanthuetndnhoanlai;
    private Long taisankhac;
    private Long cackhoanduphongruirochocactaisanconoibangkhac;

    // NỢ PHẢI TRẢ VÀ VỐN CHỦ SỞ HỮU
    private Long nophaitravavonchusohuu;
    private Long tongnophaitra;
    private Long cackhoannochinhphuvanhnn;
    private Long tienguivavaycactochuctindungkhac;
    private Long tienguicuacactctdkhac;
    private Long vaycactctdkhac;
    private Long tienguicuakhachhang;
    private Long caccongcutaichinhphaisinhvacackhoannotaichinhkhac;
    private Long vontaitrouythacdautucuachinhphuvacactochuctindungkhac;
    private Long phathanhgiaytocogia;
    private Long cackhoannokhac;
    private Long cackhoanlaiphiphaitra;
    private Long cackhoanphaitravacongnokhac;
    private Long duphongruirokhac;
    private Long thuetndnphaitra;
    private Long thuetndnhoanlaiphaitra;

    // C. Vốn chủ sở hữu
    private Long vonchusohuu;
    private Long voncuatochuctindung;
    private Long vondieule;
    private Long vondautuxdcb;
    private Long thangduvoncophan;
    private Long cophieuquy;
    private Long cophieuuudai;
    private Long vonkhac;
    private Long quycuatochuctindung;
    private Long chenhlechtygiahoidoai;
    private Long chenhlechdanhgialaitaisan;
    private Long loinhuanchuaphanphoi;
    private Long loiichcuacodongthieuso_bs;
}
