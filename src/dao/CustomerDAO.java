package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Customer;
import utils.Database;

public class CustomerDAO {


    private Customer readFromResultSet(ResultSet rs) throws SQLException {
        Customer model = new Customer();
        model.setCustomerId(rs.getInt("customer_id"));
        model.setFullName(rs.getString("full_name"));
        model.setPhoneNumber(rs.getString("phone_number"));
        model.setAddress(rs.getString("address"));
        // created_at không cần lấy ra ở đây trừ khi bạn muốn hiển thị
        return model;
    }

    
    public void insert(Customer customer) {
        String sql = "INSERT INTO Customers (full_name, phone_number, address) VALUES (?, ?, ?)";
        Database.update(sql,
                customer.getFullName(),
                customer.getPhoneNumber(),
                customer.getAddress());
    }

    
    public void update(Customer customer) {
        String sql = "UPDATE Customers SET full_name = ?, phone_number = ?, address = ? WHERE customer_id = ?";
        Database.update(sql,
                customer.getFullName(),
                customer.getPhoneNumber(),
                customer.getAddress(),
                customer.getCustomerId());
    }

    
    public void delete(int customerId) {
        String sql = "DELETE FROM Customers WHERE customer_id = ?";
        Database.update(sql, customerId);
    }

    
    public List<Customer> selectAll() {
        String sql = "SELECT * FROM Customers";
        return select(sql);
    }

   
    public Customer selectById(int customerId) {
        String sql = "SELECT * FROM Customers WHERE customer_id = ?";
        List<Customer> list = select(sql, customerId);
        return list.isEmpty() ? null : list.get(0);
    }

   
    public Customer selectByPhone(String phoneNumber) {
        String sql = "SELECT * FROM Customers WHERE phone_number = ?";
        List<Customer> list = select(sql, phoneNumber);
        return list.isEmpty() ? null : list.get(0);
    }

    
    private List<Customer> select(String sql, Object... args) {
        List<Customer> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = Database.query(sql, args);
            while (rs.next()) {
                list.add(readFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn dữ liệu Customer", e);
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
    public List<Customer> selectByKeyword(String keyword) {
    String sql = "SELECT * FROM Customers WHERE full_name LIKE ? OR phone_number LIKE ?";
    String keywordParam = "%" + keyword + "%";
    return select(sql, keywordParam, keywordParam); // Dùng lại hàm select chung
}
}
