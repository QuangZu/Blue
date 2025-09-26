package com.techtack.blue.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.techtack.blue.model.Stock;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "watchlists")
public class Watchlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    @JoinTable(name = "watchlist_stocks", joinColumns = @JoinColumn(name = "watchlist_id"), inverseJoinColumns = @JoinColumn(name = "stock_id"))
    private List<Stock> stocks = new ArrayList<>();

    public Watchlist() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Stock> getStocks() {
        return stocks;
    }

    public void setStocks(List<Stock> stocks) {
        this.stocks = stocks;
    }

    public void addStock(Stock stock) {
        if (!stocks.contains(stock)) {
            stocks.add(stock);
        }
    }

    public void removeStock(Stock stock) {
        stocks.remove(stock);
    }

    public boolean containsStock(Stock stock) {
        return stocks.contains(stock);
    }

    public boolean containsStockByCode(String code) {
        return stocks.stream().anyMatch(stock -> stock.getCode().equals(code));
    }
}