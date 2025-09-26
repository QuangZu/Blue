package com.techtack.blue.service.stock;

import com.techtack.blue.dto.StockDto;
import com.techtack.blue.dto.market.MarketSnapshotDTO;
import com.techtack.blue.dto.stock.*;
import com.techtack.blue.model.Stock;
import com.techtack.blue.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class StockService {

    private final StockRepository stockRepository;
    private final WifeedStockService wifeedStockService;
    private final WifeedCorporateService wifeedCorporateService;
    private final WifeedFinancialService wifeedFinancialService;
    private final WifeedIndustryService wifeedIndustryService;
    private final WifeedNotificationService wifeedNotificationService;
    private final com.techtack.blue.service.stock.WifeedBondsService wifeedBondsService;

    public StockService(StockRepository stockRepository,
                        WifeedStockService wifeedStockService,
                        WifeedCorporateService wifeedCorporateService,
                        WifeedFinancialService wifeedFinancialService,
                        WifeedIndustryService wifeedIndustryService,
                        WifeedNotificationService wifeedNotificationService,
                        com.techtack.blue.service.stock.WifeedBondsService wifeedBondsService) {
        this.stockRepository = stockRepository;
        this.wifeedStockService = wifeedStockService;
        this.wifeedCorporateService = wifeedCorporateService;
        this.wifeedFinancialService = wifeedFinancialService;
        this.wifeedIndustryService = wifeedIndustryService;
        this.wifeedNotificationService = wifeedNotificationService;
        this.wifeedBondsService = wifeedBondsService;
    }

    public Optional<StockDto> getStockByCode(String code) {
        Stock stock = stockRepository.findByCode(code).orElse(null);

        // Case 1: Not in DB → fetch from Wifeed, save, return
        if (stock == null) {
            ThongTinCoBanDto basicInfo = wifeedStockService.getThongTinCoBan(code);
            if (basicInfo != null) {
                StockDto fresh = convertThongTinCoBanToStockDto(basicInfo);
                stockRepository.save(convertToEntity(fresh));
                return Optional.of(fresh);
            }
            return Optional.empty();
        }

        // Case 2: Found but stale → return cached immediately, refresh async
        if (isStale(stock.getLastUpdated())) {
            StockDto cached = convertToStockDto(stock);

            // Refresh data in background without blocking
            CompletableFuture.runAsync(() -> {
                try {
                    ThongTinCoBanDto basicInfo = wifeedStockService.getThongTinCoBan(code);
                    if (basicInfo != null) {
                        StockDto fresh = convertThongTinCoBanToStockDto(basicInfo);
                        Stock updatedEntity = convertToEntity(fresh);
                        updatedEntity.setId(stock.getId()); // Preserve the ID
                        stockRepository.save(updatedEntity);
                    }
                } catch (Exception e) {
                    System.err.println("Error refreshing stock data for " + code + ": " + e.getMessage());
                }
            });

            return Optional.of(cached);
        }

        // Case 3: Still fresh → just return cached
        return Optional.of(convertToStockDto(stock));
    }

    public List<StockDto> getTopGainers() {
        List<Stock> fromDb = stockRepository.findTopGainers();
        if (!fromDb.isEmpty()) {
            return fromDb.stream().map(this::convertToStockDto).collect(Collectors.toList());
        }
        return wifeedStockService.getTopGainers();
    }

    public List<StockDto> getTopLosers() {
        List<Stock> fromDb = stockRepository.findTopLosers();
        if (!fromDb.isEmpty()) {
            return fromDb.stream().map(this::convertToStockDto).collect(Collectors.toList());
        }
        return wifeedStockService.getTopLosers();
    }

    public List<StockDto> getMostActive() {
        List<Stock> fromDb = stockRepository.findTopTradedStocks();
        if (!fromDb.isEmpty()) {
            return fromDb.stream().map(this::convertToStockDto).collect(Collectors.toList());
        }
        return wifeedStockService.getMostActive();
    }
    
    public List<StockDto> getTopTradedStocks() {
        return getMostActive();
    }

    public List<StockDto> searchStocks(String keyword) {
        List<Stock> fromDb = stockRepository.searchStocks(keyword);
        List<StockDto> results = new java.util.ArrayList<>();
        
        // Add non-stale results from DB immediately
        for (Stock stock : fromDb) {
            if (!isStale(stock.getLastUpdated())) {
                results.add(convertToStockDto(stock));
            } else {
                // For stale results, return cached but refresh async
                StockDto cached = convertToStockDto(stock);
                results.add(cached);

                CompletableFuture.runAsync(() -> {
                    try {
                        ThongTinCoBanDto basicInfo = wifeedStockService.getThongTinCoBan(stock.getCode());
                        if (basicInfo != null) {
                            StockDto fresh = convertThongTinCoBanToStockDto(basicInfo);
                            Stock updatedEntity = convertToEntity(fresh);
                            updatedEntity.setId(stock.getId()); // Preserve the ID
                            stockRepository.save(updatedEntity);
                        }
                    } catch (Exception e) {
                        System.err.println("Error refreshing stock data for " + stock.getCode() + ": " + e.getMessage());
                    }
                });
            }
        }
        
        return results;
    }

    public MarketSnapshotDTO getMarketOverview() {
        return wifeedStockService.getMarketOverview();
    }

    // ================== Hybrid Service Methods ==================

    public ThongTinCoBanDto getThongTinCoBan(String code) {
        return wifeedStockService.getThongTinCoBan(code);
    }

    public StockDto getCurrentPrice(String code) {
        return wifeedStockService.getCurrentPrice(code);
    }

    public BanLanhDaoDto getBanLanhDao(String code) {
        return wifeedStockService.getBanLanhDao(code);
    }

    public CoDongLonDto getCoDongLon(String code) {
        return wifeedStockService.getCoDongLon(code);
    }

    public CongTyConDto getCongTyCon(String code) {
        return wifeedStockService.getCongTyCon(code);
    }

    public GiaoDichNoiBoDto getGiaoDichNoiBo(String code) {
        return wifeedStockService.getGiaoDichNoiBo(code);
    }

    public LichSuKienDto getLichSuKien(String code) {
        return wifeedStockService.getLichSuKien(code);
    }

    public TyLeSoHuuNuocNgoaiDto getTyLeSoHuuNuocNgoai(String code) {
        return wifeedStockService.getTyLeSoHuuNuocNgoai(code);
    }

    public CanDoiKeToanDto getCanDoiKeToan(String code, String type, int year, int quarter) {
        return wifeedCorporateService.getCanDoiKeToan(code, type, year, quarter);
    }

    public KetQuaKinhDoanhDto getKetQuaKinhDoanh(String code, String type, int nam, int quy) {
        return wifeedCorporateService.getKetQuaKinhDoanh(code, type, nam, quy);
    }

    public LuuChuyenTienTeDto getLuuChuyenTienTe(String code, String type, int nam, int quy) {
        return wifeedCorporateService.getLuuChuyenTienTe(code, type, nam, quy);
    }

    public ThuyetMinhDto getThuyetMinh(String code, String type, int nam, int quy) {
        return wifeedCorporateService.getThuyetMinh(code, type, nam, quy);
    }

    public KeHoachKinhDoanhDto getKeHoachKinhDoanh(String code) {
        return wifeedCorporateService.getKeHoachKinhDoanh(code);
    }

    public ChiSoTaiChinhDto getChiSoTaiChinh(String code, String type) {
        return wifeedFinancialService.getChiSoTaiChinh(code, type);
    }

    public DanhSachNganhDto getDanhSachNganh() {
        return wifeedIndustryService.getDanhSachNganh();
    }

    public ChiSoTaiChinhNganhDto getChiSoTaiChinhNganhRealtime(int idNganh, int type) {
        return wifeedIndustryService.getChiSoTaiChinhNganhRealtime(idNganh, type);
    }

    public List<StockDto> getAllStocks() {
        return stockRepository.findAll().stream()
                .map(this::convertToStockDto)
                .collect(Collectors.toList());
    }

    // ================== Converter Methods ==================

    private StockDto convertToStockDto(Stock stock) {
        StockDto dto = new StockDto();
        dto.setId(stock.getId());
        dto.setCode(stock.getCode());
        dto.setName(stock.getName());
        dto.setPrice(stock.getPrice());
        dto.setVolume(stock.getVolume());
        dto.setChangeAmount(stock.getChangeAmount());
        dto.setChangePercent(stock.getChangePercent());
        dto.setFullname_vi(stock.getFullnameVi());
        dto.setLoaidn(stock.getLoaidn());
        dto.setSan(stock.getSan());
        dto.setLastUpdated(stock.getLastUpdated());
        return dto;
    }

    private StockDto convertThongTinCoBanToStockDto(ThongTinCoBanDto thongTinCoBan) {
        StockDto dto = new StockDto();
        dto.setCode(thongTinCoBan.getMack());
        // Map Vietnamese name to name field
        dto.setName(thongTinCoBan.getTen());
        // Map English name to fullname_vi field (this might need adjustment based on actual requirements)
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

    private Stock convertToEntity(StockDto dto) {
        Stock stock = new Stock();
        // Only set code if it's not null (mandatory field)
        if (dto.getCode() != null) {
            stock.setCode(dto.getCode());
        } else {
            throw new IllegalArgumentException("Stock code cannot be null");
        }
        stock.setName(dto.getName());
        stock.setPrice(dto.getPrice());
        stock.setVolume(dto.getVolume());
        stock.setChangeAmount(dto.getChangeAmount());
        stock.setChangePercent(dto.getChangePercent());
        stock.setFullnameVi(dto.getFullname_vi());
        stock.setLoaidn(dto.getLoaidn());
        stock.setSan(dto.getSan());
        stock.setExchange(dto.getExchange());
        stock.setLastUpdated(dto.getLastUpdated());
        return stock;
    }
    
    public CapNhatDuLieuDto getCapNhatDuLieu(int page, int limit) {
        return wifeedNotificationService.getCapNhatDuLieu(page, limit);
    }

    // For compatibility with WatchlistService
    public ThongTinCoBanDto getStockBasic(String code) {
        return wifeedStockService.getThongTinCoBan(code);
    }
    
    // For compatibility with WatchlistService
    public StockDto convertToDto(Stock stock) {
        return convertToStockDto(stock);
    }
    
    /**
     * Check if stock data is stale (older than 2 minutes)
     */
    private boolean isStale(LocalDateTime lastUpdated) {
        if (lastUpdated == null) {
            return true; // Consider null as stale
        }
        return lastUpdated.isBefore(LocalDateTime.now().minusMinutes(2));
    }
    
    /**
     * Fast method to get essential stock data by exchange for frontend
     */
    public List<StockDto> getStocksByExchangeWithLimit(String exchange, int limit) {
        // First try to get from database
        List<Stock> stocksFromDb = stockRepository.findActiveByExchange(exchange);
        
        if (!stocksFromDb.isEmpty() && stocksFromDb.size() >= limit) {
            // If DB has enough recent data, return it
            List<StockDto> result = stocksFromDb.stream()
                    .limit(limit)
                    .map(this::convertToStockDto)
                    .collect(java.util.stream.Collectors.toList());
            return result;
        }
        
        // If DB doesn't have enough or is empty, fetch from API
        // This is a simplified version - in practice, you might want to implement
        // a more sophisticated caching strategy
        List<StockDto> result = new java.util.ArrayList<>();
        DanhSachMaChungKhoanDto stocks = wifeedStockService.getAllStockCodes("1", exchange);
        
        if (stocks != null && stocks.getData() != null) {
            int count = 0;
            for (DanhSachMaChungKhoanDto.StockCodeInfo codeInfo : stocks.getData()) {
                if (count >= limit) break;
                if (codeInfo != null && codeInfo.getMack() != null) {
                    java.util.Optional<StockDto> stockDto = getStockByCode(codeInfo.getMack());
                    if (stockDto.isPresent()) {
                        result.add(stockDto.get());
                        count++;
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * Get EOD prices for a stock
     */
    public List<DuLieuGiaEodDto> getEodPrices(String code, String fromDate, String toDate) {
        return wifeedStockService.getEodPrices(code, fromDate, toDate);
    }
    
    /**
     * Get Vietnam market overview
     */
    public ThiTruongChungKhoanVietNamDto getVietnamMarketOverview(int page, int limit) {
        return wifeedStockService.getVietnamMarketOverview(page, limit);
    }
    
    /**
     * Get International market overview
     */
    public ThiTruongChungKhoanQuocTeDto getInternationalMarketOverview(int page, int limit) {
        return wifeedStockService.getInternationalMarketOverview(page, limit);
    }
    
    /**
     * Get Government Bonds Yield
     */
    public List<DuLieuTraiPhieuChinhPhuDto> getGovernmentBondsYield(int page, int limit) {
        return wifeedBondsService.getGovernmentBondsYield(page, limit);
    }
    
    /**
     * Get Corporate Bonds Issuers
     */
    public List<DuLieuTraiPhieuDoanhNghiepDto> getCorporateBondsIssuers() {
        return wifeedBondsService.getCorporateBondsIssuers();
    }
}
