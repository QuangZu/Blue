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

    // Denormalized fields for performance and historical tracking
    private String symbol;
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
}
