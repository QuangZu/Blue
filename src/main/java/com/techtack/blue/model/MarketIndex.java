package com.techtack.blue.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "market_indices")
@Data
public class MarketIndex {

    // Getters and setters
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    private String code;
    private double value;
    private double change;
    private double changePercent;
    private long volume;
    private LocalDateTime lastUpdated;
    
    // Default constructor required by JPA
    public MarketIndex() {
    }

}