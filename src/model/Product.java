package model;

import java.math.BigDecimal;

public class Product {
    private int productId;
    private String skuCode;
    private String productName;
    private String color;
    private String size;
    private BigDecimal retailPrice;
    private int stockQuantity;
    private String imageUrl;
    private String description;
    private int categoryId;
    private int brandId;


    public Product() {}

    public Product(int productId, String skuCode, String productName, String color, String size, BigDecimal retailPrice, int stockQuantity, String imageUrl, String description, int categoryId, int brandId) {
        this.productId = productId;
        this.skuCode = skuCode;
        this.productName = productName;
        this.color = color;
        this.size = size;
        this.retailPrice = retailPrice;
        this.stockQuantity = stockQuantity;
        this.imageUrl = imageUrl;
        this.description = description;
        this.categoryId = categoryId;
        this.brandId = brandId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public BigDecimal getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(BigDecimal retailPrice) {
        this.retailPrice = retailPrice;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }
    
    
}Nội dung cập nhật vào ngày 2025-07-18T10:00:00
