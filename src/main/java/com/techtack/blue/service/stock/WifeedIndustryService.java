package com.techtack.blue.service.stock;

import com.techtack.blue.dto.stock.ChiSoTaiChinhNganhDto;
import com.techtack.blue.dto.stock.DanhSachNganhDto;
import org.springframework.stereotype.Service;

@Service
public class WifeedIndustryService extends WifeedBaseService {

    private static final String BASE_URL = "https://wifeed.vn/api/nganh";

    public DanhSachNganhDto getDanhSachNganh() {
        return getForObject(BASE_URL + "/v2/danh-sach-nganh", DanhSachNganhDto.class);
    }

    public ChiSoTaiChinhNganhDto getChiSoTaiChinhNganhRealtime(int idNganh, int type) {
        return getForObject(BASE_URL + "/v3/chi-so-tai-chinh-nganh?type=" + type + "&idnganh=" + idNganh,
                ChiSoTaiChinhNganhDto.class);
    }
}
