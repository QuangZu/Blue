package com.techtack.blue.service.stock;

import com.techtack.blue.dto.stock.*;
import org.springframework.stereotype.Service;

@Service
public class WifeedCorporateService extends WifeedBaseService {

    private static final String BASE_URL = "https://wifeed.vn/api/tai-chinh-doanh-nghiep";

    public CanDoiKeToanDto getCanDoiKeToan(String code, String type, int year, int quarter) {
        String url = BASE_URL + "/can-doi-ke-toan?code=" + code  + "&type=" + type + "&year=" + year + "&quarter=" + quarter;
        return getForObject(url, CanDoiKeToanDto.class);
    }

    public KetQuaKinhDoanhDto getKetQuaKinhDoanh(String code, String type, int nam, int quy) {
        String url = BASE_URL + "/bctc/ket-qua-kinh-doanh"
                + "?code=" + code
                + "&type=" + type
                + "&nam=" + nam
                + "&quy=" + quy + "&";
        return getForObject(url, KetQuaKinhDoanhDto.class);
    }

    public LuuChuyenTienTeDto getLuuChuyenTienTe(String code, String type, int nam, int quy) {
        String url = BASE_URL + "/bctc/luu-chuyen-tien-te"
                + "?code=" + code
                + "&type=" + type
                + "&nam=" + nam
                + "&quy=" + quy + "&";
        return getForObject(url, LuuChuyenTienTeDto.class);
    }

    public ThuyetMinhDto getThuyetMinh(String code, String type, int nam, int quy) {
        String url = BASE_URL + "/bctc/thuyet-minh"
                + "?code=" + code
                + "&type=" + type
                + "&nam=" + nam
                + "&quy=" + quy + "&";
        return getForObject(url, ThuyetMinhDto.class);
    }

    public KeHoachKinhDoanhDto getKeHoachKinhDoanh(String code) {
        String url = BASE_URL + "/ke-hoach-kinh-doanh"
                + "?code=" + code + "&";
        return getForObject(url, KeHoachKinhDoanhDto.class);
    }
}
