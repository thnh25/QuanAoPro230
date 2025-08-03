/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dto.OrderDisplayDTO;
import dto.ThongKeDTO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import model.Order;
import utils.Database;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;

public class OrderDAO {

    private Order readFromResultSet(ResultSet rs) throws SQLException {
        return new Order(
                rs.getInt("order_id"),
                (Integer) rs.getObject("customer_id"),
                rs.getInt("user_id"),
                rs.getObject("created_at", LocalDateTime.class),
                rs.getBigDecimal("total_amount"),
                rs.getString("status"),
                rs.getString("payment_method")
        );
    }

    public List<Order> selectPendingOrders() {
        String sql = "SELECT * FROM Orders WHERE status = N'Chờ xác nhận'";

        List<Order> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = Database.query(sql);
            while (rs.next()) {
                list.add(readFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn hóa đơn chờ", e);
        } finally {
            if (rs != null) {
                try {
                    rs.getStatement().getConnection().close();
                } catch (SQLException e) {
                }
            }
        }
        return list;
    }

    public void delete(int orderId) {
        String sql = "DELETE FROM Orders WHERE order_id = ?";
        Database.update(sql, orderId);
    }

    public int insertAndGetId(Order order) {
        String sql = "INSERT INTO Orders (customer_id, user_id, created_at, total_amount, status, payment_method) VALUES (?, ?, GETDATE(), ?, ?, ?); SELECT SCOPE_IDENTITY();";
        Object generatedId = Database.value(sql,
                order.getCustomerId(),
                order.getUserId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getPaymentMethod());

        if (generatedId instanceof BigDecimal) {
            return ((BigDecimal) generatedId).intValue();
        } else if (generatedId instanceof Number) {
            return ((Number) generatedId).intValue();
        }
        throw new RuntimeException("Không thể lấy ID của hóa đơn vừa tạo.");
    }

    public List<Order> selectAll() {
        String sql = "SELECT * FROM Orders ORDER BY created_at DESC";
        return select(sql);
    }

    private List<Order> select(String sql, Object... args) {
        List<Order> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = Database.query(sql, args);
            while (rs.next()) {
                list.add(readFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn dữ liệu Order", e);
        } finally {
            if (rs != null) {
                try {
                    rs.getStatement().getConnection().close();
                } catch (SQLException e) {
                    // Bỏ qua lỗi khi đóng
                }
            }
        }
        return list;
    }

    public void updateStatus(int orderId, String newStatus) {
        String sql = "UPDATE Orders SET status = ? WHERE order_id = ?";
        Database.update(sql, newStatus, orderId);
    }

    public List<OrderDisplayDTO> selectAllForDisplay() {
        String sql = """
            SELECT
                o.order_id,
                ISNULL(c.full_name, N'Khách vãng lai') AS customer_name,
                u.full_name AS staff_name,
                o.created_at,
                o.total_amount,
                o.status
            FROM Orders o
            JOIN Users u ON o.user_id = u.user_id
            LEFT JOIN Customers c ON o.customer_id = c.customer_id
            ORDER BY o.created_at DESC
        """;

        List<OrderDisplayDTO> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = Database.query(sql);
            while (rs.next()) {
                OrderDisplayDTO dto = new OrderDisplayDTO();
                dto.setOrderId(rs.getInt("order_id"));
                dto.setCustomerName(rs.getString("customer_name"));
                dto.setStaffName(rs.getString("staff_name"));
                dto.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
                dto.setTotalAmount(rs.getBigDecimal("total_amount"));
                dto.setStatus(rs.getString("status"));
                list.add(dto);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn dữ liệu hóa đơn", e);
        } finally {
            if (rs != null) {
                try {
                    rs.getStatement().getConnection().close();
                } catch (SQLException e) {
                }
            }
        }
        return list;
    }

    public ThongKeDTO getThongKeTongQuan(Date tuNgay, Date denNgay) {
        String sql = """
            SELECT
                ISNULL(SUM(total_amount), 0) AS TongDoanhThu,
                COUNT(order_id) AS TongSoHoaDon
            FROM Orders
            WHERE status = N'Hoàn thành' AND created_at >= ? AND created_at < DATEADD(day, 1, ?)
        """;
        ThongKeDTO thongKe = new ThongKeDTO();
        ResultSet rs = null;
        try {
            rs = Database.query(sql, tuNgay, denNgay);
            if (rs.next()) {
                thongKe.setTongDoanhThu(rs.getBigDecimal("TongDoanhThu"));
                thongKe.setTongSoHoaDon(rs.getInt("TongSoHoaDon"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi lấy dữ liệu thống kê", e);
        } finally {
            if (rs != null) {
                try {
                    rs.getStatement().getConnection().close();
                } catch (SQLException e) {
                }
            }
        }
        return thongKe;
    }

    public List<OrderDisplayDTO> selectAllForDisplayByDate(Date tuNgay, Date denNgay) {
        String sql = """
        SELECT
            o.order_id,
            ISNULL(c.full_name, N'Khách vãng lai') AS customer_name,
            u.full_name AS staff_name,
            o.created_at,
            o.total_amount,
            o.status
        FROM Orders o
        JOIN Users u ON o.user_id = u.user_id
        LEFT JOIN Customers c ON o.customer_id = c.customer_id
        WHERE o.created_at >= ? AND o.created_at < DATEADD(day, 1, ?)
        ORDER BY o.created_at DESC
    """;

        List<OrderDisplayDTO> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = Database.query(sql, tuNgay, denNgay);

            while (rs.next()) {
                OrderDisplayDTO dto = new OrderDisplayDTO();
                dto.setOrderId(rs.getInt("order_id"));
                dto.setCustomerName(rs.getString("customer_name"));
                dto.setStaffName(rs.getString("staff_name"));
                dto.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
                dto.setTotalAmount(rs.getBigDecimal("total_amount"));
                dto.setStatus(rs.getString("status"));
                list.add(dto);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn dữ liệu hóa đơn theo ngày", e);
        } finally {
            if (rs != null) {
                try {
                    rs.getStatement().getConnection().close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }
}
Nội dung cập nhật vào ngày 2025-08-03T11:00:00
