package com.techtack.blue.controller;

import com.techtack.blue.dto.StockDto;
import com.techtack.blue.dto.stock.*;
import com.techtack.blue.service.stock.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stocks")
public class StockController {
    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public ResponseEntity<List<StockDto>> getAllStocks() {
        List<StockDto> allStocks = stockService.getAllStocks();
        return new ResponseEntity<>(allStocks, HttpStatus.OK);
    }
    
    @GetMapping("/top-gainers")
    public ResponseEntity<List<StockDto>> getTopGainers() {
        List<StockDto> topGainers = stockService.getTopGainers();
        return new ResponseEntity<>(topGainers, HttpStatus.OK);
    }
    
    @GetMapping("/top-losers")
    public ResponseEntity<List<StockDto>> getTopLosers() {
        List<StockDto> topLosers = stockService.getTopLosers();
        return new ResponseEntity<>(topLosers, HttpStatus.OK);
    }
    
    @GetMapping("/most-active")
    public ResponseEntity<List<StockDto>> getMostActive() {
        List<StockDto> mostActive = stockService.getMostActive();
        return new ResponseEntity<>(mostActive, HttpStatus.OK);
    }

    @GetMapping("/basic-info/{stockCode}")
    public ResponseEntity<ThongTinCoBanDto> getBasicCompanyInfo(@PathVariable String stockCode) {
        ThongTinCoBanDto basicInfo = stockService.getStockBasic(stockCode);
        return new ResponseEntity<>(basicInfo, HttpStatus.OK);
    }

    @GetMapping("/{code}")
    public ResponseEntity<StockDto> getStockByCode(@PathVariable String code) {
        return stockService.getStockByCode(code)
                .map(stockDto -> new ResponseEntity<>(stockDto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/search")
    public ResponseEntity<List<StockDto>> searchStocks(@RequestParam String keyword) {
        List<StockDto> stocks = stockService.searchStocks(keyword);
        return new ResponseEntity<>(stocks, HttpStatus.OK);
    }

    @GetMapping("/corporate/income-statement")
    public ResponseEntity<KetQuaKinhDoanhDto> getIncomeStatement(
            @RequestParam String code,
            @RequestParam String type,
            @RequestParam int nam,
            @RequestParam int quy) {
        KetQuaKinhDoanhDto result = stockService.getKetQuaKinhDoanh(code, type, nam, quy);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/corporate/cash-flow")
    public ResponseEntity<LuuChuyenTienTeDto> getCashFlow(
            @RequestParam String code,
            @RequestParam String type,
            @RequestParam int nam,
            @RequestParam int quy) {
        LuuChuyenTienTeDto result = stockService.getLuuChuyenTienTe(code, type, nam, quy);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/corporate/financial-notes")
    public ResponseEntity<ThuyetMinhDto> getFinancialNotes(
            @RequestParam String code,
            @RequestParam String type,
            @RequestParam int nam,
            @RequestParam int quy) {
        ThuyetMinhDto result = stockService.getThuyetMinh(code, type, nam, quy);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/corporate/business-plan/{stockCode}")
    public ResponseEntity<KeHoachKinhDoanhDto> getBusinessPlan(@PathVariable String stockCode) {
        KeHoachKinhDoanhDto result = stockService.getKeHoachKinhDoanh(stockCode);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/financial/balance-sheet")
    public ResponseEntity<CanDoiKeToanDto> getBalanceSheet(
            @RequestParam String code,
            @RequestParam String type,
            @RequestParam int nam,
            @RequestParam int quy) {
        CanDoiKeToanDto result = stockService.getCanDoiKeToan(code, type, nam, quy);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/financial/ratios")
    public ResponseEntity<ChiSoTaiChinhDto> getFinancialRatios(
            @RequestParam String code,
            @RequestParam String type) {
        ChiSoTaiChinhDto result = stockService.getChiSoTaiChinh(code, type);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/industry/list")
    public ResponseEntity<DanhSachNganhDto> getListOfIndustries() {
        DanhSachNganhDto result = stockService.getDanhSachNganh();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/industry/realtime-ratios")
    public ResponseEntity<ChiSoTaiChinhNganhDto> getRealtimeIndustryRatios(
            @RequestParam int idNganh,
            @RequestParam int type) {
        ChiSoTaiChinhNganhDto result = stockService.getChiSoTaiChinhNganhRealtime(idNganh, type);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/notifications/data-update")
    public ResponseEntity<CapNhatDuLieuDto> getDataUpdateNotifications(
            @RequestParam int page,
            @RequestParam int limit) {
        CapNhatDuLieuDto result = stockService.getCapNhatDuLieu(page, limit);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    
    @GetMapping("/exchange/{exchange}")
    public ResponseEntity<List<StockDto>> getStocksByExchange(
            @PathVariable String exchange,
            @RequestParam(defaultValue = "50") int limit) {
        List<StockDto> stocks = stockService.getStocksByExchangeWithLimit(exchange, limit);
        return new ResponseEntity<>(stocks, HttpStatus.OK);
    }
    
    @GetMapping("/{code}/eod")
    public ResponseEntity<List<DuLieuGiaEodDto>> getEodPrices(
            @PathVariable String code,
            @RequestParam String fromDate,
            @RequestParam String toDate) {
        List<DuLieuGiaEodDto> eodPrices = stockService.getEodPrices(code, fromDate, toDate);
        return new ResponseEntity<>(eodPrices, HttpStatus.OK);
    }
    
    @GetMapping("/vietnam-market")
    public ResponseEntity<ThiTruongChungKhoanVietNamDto> getVietnamMarketOverview(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        ThiTruongChungKhoanVietNamDto marketData = 
            stockService.getVietnamMarketOverview(page, limit);
        return new ResponseEntity<>(marketData, HttpStatus.OK);
    }
    
    @GetMapping("/international-market")
    public ResponseEntity<ThiTruongChungKhoanQuocTeDto> getInternationalMarketOverview(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        ThiTruongChungKhoanQuocTeDto marketData = 
            stockService.getInternationalMarketOverview(page, limit);
        return new ResponseEntity<>(marketData, HttpStatus.OK);
    }
}
