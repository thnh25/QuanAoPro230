package dao;

import dto.ProductDisplayDTO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Product;
import utils.Database;

public class ProductDAO {

    private Product readFromResultSet(ResultSet rs) throws SQLException {
        return new Product(
            rs.getInt("product_id"),
            rs.getString("sku_code"),
            rs.getString("product_name"),
            rs.getString("color"),
            rs.getString("size"),
            rs.getBigDecimal("retail_price"),
            rs.getInt("stock_quantity"),
            rs.getString("image_url"),
            rs.getString("description"),
            rs.getInt("category_id"),
            rs.getInt("brand_id")
        );
    }

    public void insert(Product product) {
        String sql = "INSERT INTO Products (sku_code, product_name, color, size, retail_price, stock_quantity, image_url, description, category_id, brand_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Database.update(sql, 
            product.getSkuCode(), product.getProductName(), product.getColor(),
            product.getSize(), product.getRetailPrice(), product.getStockQuantity(),
            product.getImageUrl(), product.getDescription(), product.getCategoryId(), product.getBrandId()
        );
    }

    public void update(Product product) {
        String sql = "UPDATE Products SET sku_code=?, product_name=?, color=?, size=?, retail_price=?, stock_quantity=?, image_url=?, description=?, category_id=?, brand_id=? WHERE product_id=?";
        Database.update(sql, 
            product.getSkuCode(), product.getProductName(), product.getColor(),
            product.getSize(), product.getRetailPrice(), product.getStockQuantity(),
            product.getImageUrl(), product.getDescription(), product.getCategoryId(), 
            product.getBrandId(), product.getProductId()
        );
    }
    
    public void delete(int productId) {
        String sql = "DELETE FROM Products WHERE product_id = ?";
        Database.update(sql, productId);
    }

    public List<Product> selectAll() {
        return select("SELECT * FROM Products");
    }

    public Product selectById(int productId) {
        List<Product> list = select("SELECT * FROM Products WHERE product_id = ?", productId);
        return list.isEmpty() ? null : list.get(0);
    }
    
    public Product selectBySkuCode(String skuCode) {
        List<Product> list = select("SELECT * FROM Products WHERE sku_code = ?", skuCode);
        return list.isEmpty() ? null : list.get(0);
    }
    
    private List<Product> select(String sql, Object... args) {
        List<Product> list = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = Database.query(sql, args);
            while (rs.next()) {
                list.add(readFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi truy vấn dữ liệu Product", e);
        } finally {
            if (rs != null) {
                try {
                    rs.getStatement().getConnection().close();
                } catch (SQLException e) {}
            }
        }
        return list;
    }
    public List<ProductDisplayDTO> selectAllForDisplay() {
    String sql = """
        SELECT
            p.product_id, p.sku_code, p.product_name, p.color, p.size,
            p.retail_price, p.stock_quantity,
            c.category_name, b.brand_name
        FROM Products p
        LEFT JOIN Categories c ON p.category_id = c.category_id
        LEFT JOIN Brands b ON p.brand_id = b.brand_id
        ORDER BY p.product_name
    """;
    
    List<ProductDisplayDTO> list = new ArrayList<>();
    ResultSet rs = null;
    try {
        rs = Database.query(sql);
        while (rs.next()) {
            ProductDisplayDTO dto = new ProductDisplayDTO();
            dto.setProductId(rs.getInt("product_id"));
            dto.setSkuCode(rs.getString("sku_code"));
            dto.setProductName(rs.getString("product_name"));
            dto.setColor(rs.getString("color"));
            dto.setSize(rs.getString("size"));
            dto.setRetailPrice(rs.getBigDecimal("retail_price"));
            dto.setStockQuantity(rs.getInt("stock_quantity"));
            dto.setCategoryName(rs.getString("category_name"));
            dto.setBrandName(rs.getString("brand_name"));
            list.add(dto);
        }
    } catch (SQLException e) {
        throw new RuntimeException("Lỗi truy vấn dữ liệu hiển thị sản phẩm", e);
    } finally {
        if (rs != null) {
            try {
                rs.getStatement().getConnection().close();
            } catch (SQLException e) {}
        }
    }
    return list;
}
}Nội dung cập nhật vào ngày 2025-07-20T16:45:00
