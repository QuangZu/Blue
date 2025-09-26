package com.techtack.blue.model.market;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import com.techtack.blue.model.Stock;
import java.util.List;

@Entity
@Table(name = "industries", indexes = {
    @Index(name = "idx_industry_code", columnList = "industryCode"),
    @Index(name = "idx_industry_level", columnList = "level")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Industry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 20)
    private String industryCode;
    
    @Column(nullable = false)
    private String industryName;
    
    @Column(nullable = false)
    private String industryNameEn;
    
    private Integer level; // 1, 2, 3 (Sector, Industry Group, Industry)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Industry parent;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Industry> children = new ArrayList<>();
    
    @OneToMany(mappedBy = "industry", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Stock> symbols = new ArrayList<>();
    
    // Calculated fields for market snapshot
    @Column(precision = 18, scale = 2)
    private BigDecimal marketCap;
    
    @Column(precision = 18, scale = 2)
    private BigDecimal totalVolume;
    
    @Column(precision = 18, scale = 2)
    private BigDecimal totalValue;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal changePercent;
    
    private Integer stockCount;
    
    private Integer advances;
    
    private Integer declines;
    
    private Integer unchanged;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
