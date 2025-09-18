package com.techtack.blue.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "portfolios")
@Data
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private BigDecimal totalValue;
    private BigDecimal cashBalance;

    // Getters and Setters (Lombok @Data annotation handles this)
}
