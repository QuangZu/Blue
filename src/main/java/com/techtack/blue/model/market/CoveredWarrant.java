package com.techtack.blue.model.market;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "covered_warrants", indexes = {
    @Index(name = "idx_cw_code", columnList = "warrantCode"),
    @Index(name = "idx_cw_underlying", columnList = "underlyingSymbol"),
    @Index(name = "idx_cw_issuer", columnList = "issuer")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoveredWarrant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 20)
    private String warrantCode;
    
    @Column(nullable = false)
    private String warrantName;
    
    @Column(nullable = false, length = 10)
    private String underlyingSymbol;
    
    @Column(nullable = false)
    private String issuer;
    
    @Column(length = 10)
    private String warrantType;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal exercisePrice;
    
    @Column(precision = 10, scale = 4)
    private BigDecimal exerciseRatio;
    
    private LocalDate issueDate;
    
    private LocalDate maturityDate;
    
    private LocalDate lastTradingDate;
    
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
    
    @Column(precision = 18, scale = 2)
    private BigDecimal value;
    
    @Column(precision = 18, scale = 0)
    private BigDecimal outstandingVolume;
    
    private Boolean isActive;
    
    @Column(length = 10)
    private String exchange;
    
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
