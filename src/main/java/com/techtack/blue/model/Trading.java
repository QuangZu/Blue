package com.techtack.blue.model;

import java.time.LocalDate;
import java.time.LocalTime;

import com.techtack.blue.model.order.OrderSide;
import com.techtack.blue.model.order.OrderStatus;
import com.techtack.blue.model.order.OrderType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Entity
@Table(name = "tradings")
@Data
public class Trading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Reference to the original order
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private String code;
    private String accountId;
    
    @Min(0)
    private Double quantity;

    @DecimalMin("0.0")
    private Double price;
    
    @DecimalMin("0.0")
    private Double triggerPrice;
    
    @DecimalMin("0.0")
    private Double trailingAmount;

    @DecimalMin("0.0")
    private Double takeProfitPrice;

    @DecimalMin("0.0")
    private Double cutLossPrice;

    @DecimalMin("0.0")
    private Double toler;

    // Use enums for consistency
    @Enumerated(EnumType.STRING)
    private OrderType orderType;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    @Enumerated(EnumType.STRING)
    private OrderSide orderSide;
    
    private Double marketPrice;
    private Long totalVolume;
    
    private LocalDate effectiveDate;
    private LocalDate expiryDate;
    private LocalTime createdAt;
    
    // Unified naming convention
    private Double buyingPower;
    private Double maxQuantity;
    
    // Execution tracking fields
    private Double executedQuantity;
    private Double executedPrice;
    private LocalTime executedAt;
    private LocalDate executedDate;
    
    // Helper methods
    public boolean isFullyExecuted() {
        return executedQuantity != null && executedQuantity.equals(quantity);
    }
    
    public boolean isPartiallyExecuted() {
        return executedQuantity != null && executedQuantity > 0 && executedQuantity < quantity;
    }
    
    public Double getRemainingQuantity() {
        if (executedQuantity == null) return quantity;
        return quantity - executedQuantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getTriggerPrice() {
        return triggerPrice;
    }

    public void setTriggerPrice(Double triggerPrice) {
        this.triggerPrice = triggerPrice;
    }

    public Double getTrailingAmount() {
        return trailingAmount;
    }

    public void setTrailingAmount(Double trailingAmount) {
        this.trailingAmount = trailingAmount;
    }

    public Double getTakeProfitPrice() {
        return takeProfitPrice;
    }

    public void setTakeProfitPrice(Double takeProfitPrice) {
        this.takeProfitPrice = takeProfitPrice;
    }

    public Double getCutLossPrice() {
        return cutLossPrice;
    }

    public void setCutLossPrice(Double cutLossPrice) {
        this.cutLossPrice = cutLossPrice;
    }

    public Double getToler() {
        return toler;
    }

    public void setToler(Double toler) {
        this.toler = toler;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public OrderSide getOrderSide() {
        return orderSide;
    }

    public void setOrderSide(OrderSide orderSide) {
        this.orderSide = orderSide;
    }

    public Double getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(Double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public Long getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(Long totalVolume) {
        this.totalVolume = totalVolume;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public LocalTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalTime createdAt) {
        this.createdAt = createdAt;
    }

    public Double getBuyingPower() {
        return buyingPower;
    }

    public void setBuyingPower(Double buyingPower) {
        this.buyingPower = buyingPower;
    }

    public Double getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(Double maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public Double getExecutedQuantity() {
        return executedQuantity;
    }

    public void setExecutedQuantity(Double executedQuantity) {
        this.executedQuantity = executedQuantity;
    }

    public Double getExecutedPrice() {
        return executedPrice;
    }

    public void setExecutedPrice(Double executedPrice) {
        this.executedPrice = executedPrice;
    }

    public LocalTime getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(LocalTime executedAt) {
        this.executedAt = executedAt;
    }

    public LocalDate getExecutedDate() {
        return executedDate;
    }

    public void setExecutedDate(LocalDate executedDate) {
        this.executedDate = executedDate;
    }
}
