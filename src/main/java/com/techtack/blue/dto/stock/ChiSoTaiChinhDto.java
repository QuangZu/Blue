package com.techtack.blue.dto.stock;

import lombok.Data;

@Data
public class ChiSoTaiChinhDto {
    private String code;          // Mã cổ phiếu
    private Integer nam;          // Năm
    private Integer quy;          // Quý (0 = năm, 1-4 = quý)

    // Hiệu quả sinh lời
    private Double eps;           // Lợi nhuận trên cổ phiếu
    private Double pe;            // P/E ratio
    private Double pb;            // P/B ratio
    private Double roe;           // ROE (%)
    private Double roa;           // ROA (%)

    // Quy mô & đòn bẩy
    private Double vonHoa;        // Vốn hóa thị trường
    private Double noTrenVonChu;  // Nợ / Vốn chủ sở hữu

    // Chỉ số đặc thù ngân hàng
    private Double nim;           // Net Interest Margin
    private Double cir;           // Cost-to-Income Ratio
    private Double tyLeNoXau;     // Nợ xấu (%)

    // Tăng trưởng
    private Double doanhThuYoY;   // Doanh thu tăng trưởng YoY
    private Double loiNhuanYoY;   // Lợi nhuận tăng trưởng YoY

    // Optional: chia cổ tức
    private Double dividendYield; // Tỷ suất cổ tức (%)
}
