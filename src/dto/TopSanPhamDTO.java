package dto;

public class TopSanPhamDTO {
    private String tenSanPham;
    private String skuCode;
    private int soLuongDaBan;

    public String getTenSanPham() {
        return tenSanPham;
    }
    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }
    public String getSkuCode() {
        return skuCode;
    }
    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }
    public int getSoLuongDaBan() {
        return soLuongDaBan;
    }
    public void setSoLuongDaBan(int soLuongDaBan) {
        this.soLuongDaBan = soLuongDaBan;
    }
}