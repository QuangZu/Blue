package com.techtack.blue.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="stocks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    private String name;
    private Double price;
    private Double previousClose;
    private Long volume;
    private Double marketCap;
    private Double changeAmount;
    private Double changePercent;
    private String fullnameVi;
    private String loaidn;
    private String san;
    private String exchange;
    private Long soluongluuhanh;

    private LocalDateTime lastUpdated;
    
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "industry_id")
    private com.techtack.blue.model.market.Industry industry;
}
