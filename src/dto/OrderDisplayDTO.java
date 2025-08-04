package dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderDisplayDTO {

    private int orderId;
    private String customerName;
    private String staffName;
    private LocalDateTime createdAt;
    private BigDecimal totalAmount;
    private String status;

    public OrderDisplayDTO() {
    }

    public OrderDisplayDTO(int orderId, String customerName, String staffName, LocalDateTime createdAt, BigDecimal totalAmount, String status) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.staffName = staffName;
        this.createdAt = createdAt;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
