package com.techtack.blue.model.market;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "derivatives", indexes = {
    @Index(name = "idx_derivative_code", columnList = "derivativeCode"),
    @Index(name = "idx_derivative_underlying", columnList = "underlyingSymbol"),
    @Index(name = "idx_derivative_expiry", columnList = "expiryDate")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Derivative {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 20)
    private String derivativeCode;
    
    @Column(nullable = false)
    private String derivativeName;
    
    @Column(nullable = false, length = 20)
    private String derivativeType; // FUTURE, OPTION
    
    @Column(nullable = false, length = 10)
    private String underlyingSymbol; // VN30, Stock symbol
    
    private LocalDate expiryDate;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal strikePrice; // For options
    
    @Column(length = 10)
    private String optionType; // CALL, PUT (for options)
    
    @Column(precision = 10, scale = 0)
    private BigDecimal contractSize;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal lastPrice;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal referencePrice;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal ceilingPrice;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal floorPrice;
    
    @Column(precision = 18, scale = 0)
    private BigDecimal volume;
    
    @Column(precision = 18, scale = 0)
    private BigDecimal openInterest;
    
    private Boolean isActive;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
