package com.techtack.blue.service.stock;

import com.techtack.blue.dto.stock.LichSuKienDto;
import com.techtack.blue.dto.stock.CapNhatDuLieuDto;
import org.springframework.stereotype.Service;

@Service
public class WifeedNotificationService extends WifeedBaseService {
    private static final String BASE_URL = "https://wifeed.vn/api/thong-bao-api";

    public CapNhatDuLieuDto getCapNhatDuLieu(int page, int limit) {
        return getForObject(BASE_URL + "/cap-nhat-du-lieu?page=" + page + "&limit=" + limit,
                CapNhatDuLieuDto.class);
    }
    public LichSuKienDto getLichSuKien(String code) {
        return getForObject(BASE_URL + "/lich-su-kien?code=" + code, LichSuKienDto.class);
    }

}
