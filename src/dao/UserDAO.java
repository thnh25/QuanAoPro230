package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.User;
import utils.Database;

public class UserDAO {

    private User readFromResultSet(ResultSet rs) throws SQLException {
        User model = new User();
        model.setUserId(rs.getInt("user_id"));
        model.setUsername(rs.getString("username"));
        model.setPassword(rs.getString("password_hash"));
        model.setFullName(rs.getString("full_name"));
        model.setPhoneNumber(rs.getString("phone_number"));
        model.setGender(rs.getString("gender"));
        return model;
    }

    public void insert(User user) {
        String sql = "INSERT INTO Users (username, password_hash, full_name, phone_number, gender, role_id, is_active) VALUES (?, ?, ?, ?, ?, 2, 1)";
        Database.update(sql,
                user.getUsername(),
                user.getPassword(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getGender());
    }

    public void update(User user) {
        String sql = "UPDATE Users SET username=?, password_hash=?, full_name=?, phone_number=?, gender=? WHERE user_id=?";
        Database.update(sql,
                user.getUsername(),
                user.getPassword(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getGender(),
                user.getUserId());
    }
    
    // Nút Xóa bây giờ sẽ là xóa thật
    public void delete(int userId) {
        String sql = "DELETE FROM Users WHERE user_id = ?";
        Database.update(sql, userId);
    }

    public List<User> selectAll() {
        String sql = "SELECT * FROM Users WHERE role_id = 2"; // Chỉ lấy nhân viên
        return select(sql);
    }

    public User selectById(int userId) {
        String sql = "SELECT * FROM Users WHERE user_id = ?";
        List<User> list = select(sql, userId);
        return list.isEmpty() ? null : list.get(0);
    }
    
    public User selectByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE username = ?";
        List<User> list = select(sql, username);
        return list.isEmpty() ? null : list.get(0);
    }

    private List<User> select(String sql, Object... args) {
        List<User> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = Database.query(sql, args);
            while (rs.next()) {
                list.add(readFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn dữ liệu User", e);
        } finally {
            if (rs != null) {
                try {
                    rs.getStatement().getConnection().close();
                } catch (SQLException e) {}
            }
        }
        return list;
    }
}