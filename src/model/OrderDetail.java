package model;

import java.math.BigDecimal;

public class OrderDetail {

    private int orderDetailId;
    private int orderId;
    private int productId; // Đã được cập nhật từ variantId
    private int quantity;
    private BigDecimal pricePerUnit; // Lưu lại giá tại thời điểm bán

    // Constructor rỗng
    public OrderDetail() {
    }

    // Constructor đầy đủ tham số
    public OrderDetail(int orderDetailId, int orderId, int productId, int quantity, BigDecimal pricePerUnit) {
        this.orderDetailId = orderDetailId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
    }

    // --- Getters and Setters ---
    public int getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(int orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }
}
