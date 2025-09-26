package com.techtack.blue.service.stock;

import com.techtack.blue.dto.stock.CanDoiKeToanDto;
import com.techtack.blue.dto.stock.ChiSoTaiChinhDto;
import org.springframework.stereotype.Service;

@Service
public class WifeedFinancialService extends WifeedBaseService {

    private static final String BASE_URL = "https://wifeed.vn/api/tai-chinh-doanh-nghiep";

    public CanDoiKeToanDto getCanDoiKeToan(String code, String type, int nam, int quy) {
        return getForObject(BASE_URL + "/bctc/can-doi-ke-toan?code=" + code + "&type=" + type + "&nam=" + nam + "&quy=" + quy,
                CanDoiKeToanDto.class);
    }

    public ChiSoTaiChinhDto getChiSoTaiChinh(String code, String type) {
        return getForObject(BASE_URL + "/v2/chi-so-tai-chinh?code=" + code + "&type=" + type, ChiSoTaiChinhDto.class);
    }
}

