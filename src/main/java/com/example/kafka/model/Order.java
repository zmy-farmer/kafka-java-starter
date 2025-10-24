package com.example.kafka.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
public class Order {
    @JsonProperty("orderId")
    private String orderId;
    
    @JsonProperty("userId")
    private String userId;
    
    @JsonProperty("productName")
    private String productName;
    
    @JsonProperty("amount")
    private double amount;
    
    @JsonProperty("timestamp")
    private LocalDateTime timestamp;
    
    public Order() {}
    
    public Order(String orderId, String userId, String productName, double amount) {
        this.orderId = orderId;
        this.userId = userId;
        this.productName = productName;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", userId='" + userId + '\'' +
                ", productName='" + productName + '\'' +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                '}';
    }
}
