package com.techtack.blue.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trading_accounts")
@Data
public class TradingAccount {

    public enum AccountType {
        CASH,
        MARGIN
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private String accountNumber;

    @Column(nullable = false)
    private String accountName;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private BigDecimal totalNAV = BigDecimal.ZERO;
    private BigDecimal totalAssets = BigDecimal.ZERO;
    private BigDecimal cashBalance = BigDecimal.ZERO;
    private BigDecimal buyingPower = BigDecimal.ZERO;
    private BigDecimal withdrawable = BigDecimal.ZERO;
    private BigDecimal right_subscription = BigDecimal.ZERO;
    private BigDecimal stockNAV = BigDecimal.ZERO;
    private BigDecimal fundNAV = BigDecimal.ZERO;
    private BigDecimal investmentProductsNAV = BigDecimal.ZERO;
    private BigDecimal t0buy_value = BigDecimal.ZERO;
    private BigDecimal t0sell_value = BigDecimal.ZERO;
    private BigDecimal t1buy_value = BigDecimal.ZERO;
    private BigDecimal t1sell_value = BigDecimal.ZERO;
    private BigDecimal unmatched_buy_value = BigDecimal.ZERO;
    private BigDecimal unmatched_sell_value = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal availableAdvancedCash = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal cashDividend = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal liabilities = BigDecimal.ZERO;

    private boolean isPrimary;
    private boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public void setUser(User user) {
        this.user = user;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public BigDecimal getTotalNAV() {
        return totalNAV;
    }

    public void setTotalNAV(BigDecimal totalNAV) {
        this.totalNAV = totalNAV;
    }

    public BigDecimal getTotalAssets() {
        return totalAssets;
    }

    public void setTotalAssets(BigDecimal totalAssets) {
        this.totalAssets = totalAssets;
    }

    public BigDecimal getCashBalance() {
        return cashBalance;
    }

    public void setCashBalance(BigDecimal cashBalance) {
        this.cashBalance = cashBalance;
    }

    public BigDecimal getBuyingPower() {
        return buyingPower;
    }

    public void setBuyingPower(BigDecimal buyingPower) {
        this.buyingPower = buyingPower;
    }

    public BigDecimal getWithdrawable() {
        return withdrawable;
    }

    public void setWithdrawable(BigDecimal withdrawable) {
        this.withdrawable = withdrawable;
    }

    public BigDecimal getRight_subscription() {
        return right_subscription;
    }

    public void setRight_subscription(BigDecimal right_subscription) {
        this.right_subscription = right_subscription;
    }

    public BigDecimal getStockNAV() {
        return stockNAV;
    }

    public void setStockNAV(BigDecimal stockNAV) {
        this.stockNAV = stockNAV;
    }

    public BigDecimal getFundNAV() {
        return fundNAV;
    }

    public void setFundNAV(BigDecimal fundNAV) {
        this.fundNAV = fundNAV;
    }

    public BigDecimal getInvestmentProductsNAV() {
        return investmentProductsNAV;
    }

    public void setInvestmentProductsNAV(BigDecimal investmentProductsNAV) {
        this.investmentProductsNAV = investmentProductsNAV;
    }

    public BigDecimal getT0buy_value() {
        return t0buy_value;
    }

    public void setT0buy_value(BigDecimal t0buy_value) {
        this.t0buy_value = t0buy_value;
    }

    public BigDecimal getT0sell_value() {
        return t0sell_value;
    }

    public void setT0sell_value(BigDecimal t0sell_value) {
        this.t0sell_value = t0sell_value;
    }

    public BigDecimal getT1buy_value() {
        return t1buy_value;
    }

    public void setT1buy_value(BigDecimal t1buy_value) {
        this.t1buy_value = t1buy_value;
    }

    public BigDecimal getT1sell_value() {
        return t1sell_value;
    }

    public void setT1sell_value(BigDecimal t1sell_value) {
        this.t1sell_value = t1sell_value;
    }

    public BigDecimal getUnmatched_buy_value() {
        return unmatched_buy_value;
    }

    public void setUnmatched_buy_value(BigDecimal unmatched_buy_value) {
        this.unmatched_buy_value = unmatched_buy_value;
    }

    public BigDecimal getUnmatched_sell_value() {
        return unmatched_sell_value;
    }

    public void setUnmatched_sell_value(BigDecimal unmatched_sell_value) {
        this.unmatched_sell_value = unmatched_sell_value;
    }

    public BigDecimal getAvailableAdvancedCash() {
        return availableAdvancedCash;
    }

    public void setAvailableAdvancedCash(BigDecimal availableAdvancedCash) {
        this.availableAdvancedCash = availableAdvancedCash;
    }

    public BigDecimal getCashDividend() {
        return cashDividend;
    }

    public void setCashDividend(BigDecimal cashDividend) {
        this.cashDividend = cashDividend;
    }

    public BigDecimal getLiabilities() {
        return liabilities;
    }

    public void setLiabilities(BigDecimal liabilities) {
        this.liabilities = liabilities;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public boolean isActive() {
        return isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}