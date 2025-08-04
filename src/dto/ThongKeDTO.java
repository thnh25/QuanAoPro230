package dto;

import java.math.BigDecimal;

public class ThongKeDTO {
    private BigDecimal tongDoanhThu;
    private int tongSoHoaDon;
    private int tongSanPhamDaBan;

    public BigDecimal getTongDoanhThu() {
        return tongDoanhThu;
    }
    public void setTongDoanhThu(BigDecimal tongDoanhThu) {
        this.tongDoanhThu = tongDoanhThu;
    }
    public int getTongSoHoaDon() {
        return tongSoHoaDon;
    }
    public void setTongSoHoaDon(int tongSoHoaDon) {
        this.tongSoHoaDon = tongSoHoaDon;
    }
    public int getTongSanPhamDaBan() {
        return tongSanPhamDaBan;
    }
    public void setTongSanPhamDaBan(int tongSanPhamDaBan) {
        this.tongSanPhamDaBan = tongSanPhamDaBan;
    }
}