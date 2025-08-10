package dao;

import dto.OrderDetailDisplayDTO;
import dto.TopSanPhamDTO;
import java.math.BigDecimal;
import model.OrderDetail;
import utils.Database;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderDetailDAO {


    private OrderDetail readFromResultSet(ResultSet rs) throws SQLException {
        OrderDetail model = new OrderDetail();
        model.setOrderDetailId(rs.getInt("order_detail_id"));
        model.setOrderId(rs.getInt("order_id"));
        model.setProductId(rs.getInt("product_id")); // Sử dụng product_id
        model.setQuantity(rs.getInt("quantity"));
        model.setPricePerUnit(rs.getBigDecimal("price_per_unit"));
        return model;
    }


    public void insert(OrderDetail detail) {
        String sql = "INSERT INTO OrderDetails (order_id, product_id, quantity, price_per_unit) VALUES (?, ?, ?, ?)";
        Database.update(sql,
                detail.getOrderId(),
                detail.getProductId(), // Sử dụng product_id
                detail.getQuantity(),
                detail.getPricePerUnit());
    }

    
    public List<OrderDetail> selectByOrderId(int orderId) {
        String sql = "SELECT * FROM OrderDetails WHERE order_id = ?";
        List<OrderDetail> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = Database.query(sql, orderId);
            while (rs.next()) {
                list.add(readFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn dữ liệu OrderDetail", e);
        } finally {
            if (rs != null) {
                try {
                    rs.getStatement().getConnection().close();
                } catch (SQLException e) {
                    // Bỏ qua lỗi khi đóng kết nối
                }
            }
        }
        return list;
    }
    public List<OrderDetailDisplayDTO> selectDetailsForDisplay(int orderId) {
        String sql = """
            SELECT
                p.sku_code,
                p.product_name,
                od.quantity,
                od.price_per_unit
            FROM OrderDetails od
            JOIN Products p ON od.product_id = p.product_id
            WHERE od.order_id = ?
        """;
        
        List<OrderDetailDisplayDTO> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = Database.query(sql, orderId);
            while (rs.next()) {
                OrderDetailDisplayDTO dto = new OrderDetailDisplayDTO();
                dto.setSkuCode(rs.getString("sku_code"));
                dto.setProductName(rs.getString("product_name"));
                dto.setQuantity(rs.getInt("quantity"));
                dto.setPricePerUnit(rs.getBigDecimal("price_per_unit"));
                dto.setLineTotal(dto.getPricePerUnit().multiply(new BigDecimal(dto.getQuantity())));
                list.add(dto);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn chi tiết hóa đơn", e);
        } finally {
            if (rs != null) {
                try {
                    rs.getStatement().getConnection().close();
                } catch (SQLException e) {}
            }
        }
        return list;
    }
    public List<TopSanPhamDTO> getTopSanPhamBanChay(Date tuNgay, Date denNgay) {
        String sql = """
            SELECT TOP 10
                p.product_name,
                p.sku_code,
                SUM(od.quantity) AS SoLuongDaBan
            FROM OrderDetails od
            JOIN Products p ON od.product_id = p.product_id
            JOIN Orders o ON od.order_id = o.order_id
            WHERE o.status = N'Hoàn thành' AND o.created_at BETWEEN ? AND ?
            GROUP BY p.product_name, p.sku_code
            ORDER BY SoLuongDaBan DESC
        """;
        
        List<TopSanPhamDTO> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = Database.query(sql, tuNgay, denNgay);
            while (rs.next()) {
                TopSanPhamDTO dto = new TopSanPhamDTO();
                dto.setTenSanPham(rs.getString("product_name"));
                dto.setSkuCode(rs.getString("sku_code"));
                dto.setSoLuongDaBan(rs.getInt("SoLuongDaBan"));
                list.add(dto);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy top sản phẩm", e);
        } finally {
            if (rs != null) {
                try { rs.getStatement().getConnection().close(); } catch (SQLException e) {}
            }
        }
        return list;
    }
    
    public int getTongSanPhamDaBan(Date tuNgay, Date denNgay) {
        String sql = """
            SELECT ISNULL(SUM(od.quantity), 0) FROM OrderDetails od
            JOIN Orders o ON od.order_id = o.order_id
            WHERE o.status = N'Đã thanh toán' AND o.created_at BETWEEN ? AND ?
        """;
        Object result = Database.value(sql, tuNgay, denNgay);
        return (result != null) ? (int) result : 0;
    }
}Nội dung cập nhật vào ngày 2025-08-10T16:00:00
