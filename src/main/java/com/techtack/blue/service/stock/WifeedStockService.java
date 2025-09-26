package com.techtack.blue.service.stock;

import com.techtack.blue.dto.StockDto;
import com.techtack.blue.dto.market.MarketSnapshotDTO;
import com.techtack.blue.dto.stock.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

@Service
public class WifeedStockService extends WifeedBaseService {

    private static final String BASE_URL = "https://wifeed.vn/api/thong-tin-co-phieu";
    private static final String MARKET_URL = "https://wifeed.vn/api/thi-truong";

    public ThongTinCoBanDto getThongTinCoBan(String code) {
        return getForObject(BASE_URL + "/thong-tin-co-ban?code=" + code, ThongTinCoBanDto.class);
    }

    public StockDto getCurrentPrice(String code) {
        String url = "https://wifeed.vn/api/real-time/stock-m-chart?code=" + code;
        return getForObject(url, StockDto.class);
    }

    public List<StockDto> getTopTradedStocks() {
        String url = MARKET_URL + "/top-giao-dich";
        StockDto[] result = getForObject(url, StockDto[].class);
        return Arrays.asList(result);
    }

    public List<StockDto> getTopGainers() {
        String url = MARKET_URL + "/top-tang-gia";
        StockDto[] result = getForObject(url, StockDto[].class);
        return Arrays.asList(result);
    }

    public List<StockDto> getTopLosers() {
        String url = MARKET_URL + "/top-giam-gia";
        StockDto[] result = getForObject(url, StockDto[].class);
        return Arrays.asList(result);
    }

    public List<StockDto> getMostActive() {
        return getTopTradedStocks();
    }

    public List<StockDto> searchStocks(String keyword) {
        // For now, return an empty list as a placeholder
        // The original implementation was too slow as it fetches from all exchanges
        return new ArrayList<>();
    }
    
    public DanhSachMaChungKhoanDto getAllStockCodes(String loaidn, String san) {
        String url = BASE_URL + "/danh-sach-ma-chung-khoan?loaidn=" + loaidn + "&san=" + san;
        return getForObject(url, DanhSachMaChungKhoanDto.class);
    }
    
    public List<StockDto> fetchStocksByCodes(DanhSachMaChungKhoanDto danhSach) {
        if (danhSach == null || danhSach.getData() == null) {
            return Arrays.asList();
        }
        
        List<StockDto> stocks = new ArrayList<>();
        for (DanhSachMaChungKhoanDto.StockCodeInfo codeInfo : danhSach.getData()) {
            if (codeInfo != null && codeInfo.getMack() != null) {
                ThongTinCoBanDto thongTin = getThongTinCoBan(codeInfo.getMack());
                if (thongTin != null) {
                    StockDto dto = convertThongTinCoBanToStockDto(thongTin);
                    stocks.add(dto);
                }
            }
        }
        return stocks;
    }
    
    private StockDto convertThongTinCoBanToStockDto(ThongTinCoBanDto thongTinCoBan) {
        StockDto dto = new StockDto();
        dto.setCode(thongTinCoBan.getMack());
        dto.setName(thongTinCoBan.getTen());
        dto.setFullname_vi(thongTinCoBan.getName());
        dto.setLoaidn(thongTinCoBan.getLoai_hinh_cong_ty());
        dto.setSan(thongTinCoBan.getSan_niem_yet());
        dto.setExchange(thongTinCoBan.getSan_niem_yet());

        if (thongTinCoBan.getSmg() != null) {
            dto.setPrice(thongTinCoBan.getSmg().doubleValue());
        }
        if (thongTinCoBan.getVolume_daily() != null) {
            dto.setVolume(thongTinCoBan.getVolume_daily());
        }
        if (thongTinCoBan.getDif() != null) {
            dto.setChangeAmount(thongTinCoBan.getDif().doubleValue());
        }
        if (thongTinCoBan.getDif_percent() != null) {
            dto.setChangePercent(thongTinCoBan.getDif_percent().doubleValue());
        }
        
        return dto;
    }
    
    

    public MarketSnapshotDTO getMarketOverview() {
        // Try different possible endpoints for market overview
        String[] possibleUrls = {
            MARKET_URL + "/tong-quan",
            MARKET_URL + "/overview", 
            MARKET_URL + "/market-overview",
            MARKET_URL + "/summary",
            "https://wifeed.vn/api/thi-truong-vn/overview"  // Alternative endpoint
        };
        
        for (String url : possibleUrls) {
            try {
                String finalUrl = url.contains("?") ? url + "&apikey=" + stock_api_key : url + "?apikey=" + stock_api_key;
                System.out.println("Trying market overview URL: " + finalUrl);
                
                MarketSnapshotDTO result = getForObject(url, MarketSnapshotDTO.class);
                if (result != null) {
                    return result;
                }
            } catch (Exception e) {
                System.out.println("Failed to fetch market overview from: " + url + ", error: " + e.getMessage());
                continue; // Try next URL
            }
        }
        
        // If all endpoints fail, return a default MarketSnapshotDTO
        System.out.println("All market overview endpoints failed, returning default");
        MarketSnapshotDTO defaultSnapshot = new MarketSnapshotDTO();
        return defaultSnapshot;
    }
    
    // EOD Prices - End of Day historical prices
    public List<DuLieuGiaEodDto> getEodPrices(String code, String fromDate, String toDate) {
        String url = "https://wifeed.vn/api/du-lieu-gia-eod/full?code=" + code + "&from-date=" + fromDate + "&to-date=" + toDate;
        return Arrays.asList(getForObject(url, DuLieuGiaEodDto[].class));
    }
    
    // Vietnam Market Overview
    public ThiTruongChungKhoanVietNamDto getVietnamMarketOverview(int page, int limit) {
        String url = "https://wifeed.vn/api/kinh-te-vi-mo/co-phieu/viet-nam?apikey=" + stock_api_key + "&page=" + page + "&limit=" + limit;
        // Use the base method but ensure it doesn't try to add API key again by making it think it already has it
        return getForObject(url, ThiTruongChungKhoanVietNamDto.class);
    }
    
    // International Market Overview
    public ThiTruongChungKhoanQuocTeDto getInternationalMarketOverview(int page, int limit) {
        String url = "https://wifeed.vn/api/kinh-te-vi-mo/co-phieu/the-gioi?apikey=" + stock_api_key + "&page=" + page + "&limit=" + limit;
        // Use the base method but ensure it doesn't try to add API key again by making it think it already has it
        return getForObject(url, ThiTruongChungKhoanQuocTeDto.class);
    }

    public BanLanhDaoDto getBanLanhDao(String code) {
        return getForObject(BASE_URL + "/ban-lanh-dao?code=" + code, BanLanhDaoDto.class);
    }

    public CoDongLonDto getCoDongLon(String code) {
        return getForObject(BASE_URL + "/co-dong-lon?code=" + code + "&time=now", CoDongLonDto.class);
    }

    public CongTyConDto getCongTyCon(String code) {
        return getForObject(BASE_URL + "/v2/sub-company?code=" + code, CongTyConDto.class);
    }

    public GiaoDichNoiBoDto getGiaoDichNoiBo(String code) {
        return getForObject(BASE_URL + "/giao-dich-noi-bo?code=" + code, GiaoDichNoiBoDto.class);
    }

    public LichSuKienDto getLichSuKien(String code) {
        return getForObject(BASE_URL + "/lich-su-kien?code=" + code, LichSuKienDto.class);
    }

    public TyLeSoHuuNuocNgoaiDto getTyLeSoHuuNuocNgoai(String code) {
        return getForObject(BASE_URL + "/ty-le-so-huu-nuoc-ngoai?code=" + code, TyLeSoHuuNuocNgoaiDto.class);
    }
}