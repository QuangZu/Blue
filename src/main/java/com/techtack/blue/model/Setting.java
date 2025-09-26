package com.techtack.blue.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "setting")
@Data
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
    
    // Security Settings
    private boolean savePinOtp = false;
    private boolean faceIdTouchIdEnabled = false;
    private boolean twoFactorAuthEnabled = false;
    private String smartOtpMethod = "SMS";
    
    // Application Settings
    private boolean notificationsEnabled = true;
    private boolean lightTheme = false; // dark = true
    private String language = "EN";
    private String mainScreen = "HOME";
    private boolean hideIndexChart = false;
    
    // Notification Preferences
    private boolean priceAlerts = true;
    private boolean orderNotifications = true;
    private boolean marketNews = true;
    private boolean systemNotifications = true;
    
    // Trading Preferences
    private String defaultOrderType = "MARKET";
    private boolean confirmOrders = true;
    private double defaultOrderAmount = 0.0;
    
    // Privacy Settings
    private boolean shareAnalytics = false;
    private boolean marketingEmails = false;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isSavePinOtp() {
        return savePinOtp;
    }

    public void setSavePinOtp(boolean savePinOtp) {
        this.savePinOtp = savePinOtp;
    }

    public boolean isFaceIdTouchIdEnabled() {
        return faceIdTouchIdEnabled;
    }

    public void setFaceIdTouchIdEnabled(boolean faceIdTouchIdEnabled) {
        this.faceIdTouchIdEnabled = faceIdTouchIdEnabled;
    }

    public boolean isTwoFactorAuthEnabled() {
        return twoFactorAuthEnabled;
    }

    public void setTwoFactorAuthEnabled(boolean twoFactorAuthEnabled) {
        this.twoFactorAuthEnabled = twoFactorAuthEnabled;
    }

    public String getSmartOtpMethod() {
        return smartOtpMethod;
    }

    public void setSmartOtpMethod(String smartOtpMethod) {
        this.smartOtpMethod = smartOtpMethod;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public boolean isLightTheme() {
        return lightTheme;
    }

    public void setLightTheme(boolean lightTheme) {
        this.lightTheme = lightTheme;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getMainScreen() {
        return mainScreen;
    }

    public void setMainScreen(String mainScreen) {
        this.mainScreen = mainScreen;
    }

    public boolean isHideIndexChart() {
        return hideIndexChart;
    }

    public void setHideIndexChart(boolean hideIndexChart) {
        this.hideIndexChart = hideIndexChart;
    }

    public boolean isPriceAlerts() {
        return priceAlerts;
    }

    public void setPriceAlerts(boolean priceAlerts) {
        this.priceAlerts = priceAlerts;
    }

    public boolean isOrderNotifications() {
        return orderNotifications;
    }

    public void setOrderNotifications(boolean orderNotifications) {
        this.orderNotifications = orderNotifications;
    }

    public boolean isMarketNews() {
        return marketNews;
    }

    public void setMarketNews(boolean marketNews) {
        this.marketNews = marketNews;
    }

    public boolean isSystemNotifications() {
        return systemNotifications;
    }

    public void setSystemNotifications(boolean systemNotifications) {
        this.systemNotifications = systemNotifications;
    }

    public String getDefaultOrderType() {
        return defaultOrderType;
    }

    public void setDefaultOrderType(String defaultOrderType) {
        this.defaultOrderType = defaultOrderType;
    }

    public boolean isConfirmOrders() {
        return confirmOrders;
    }

    public void setConfirmOrders(boolean confirmOrders) {
        this.confirmOrders = confirmOrders;
    }

    public double getDefaultOrderAmount() {
        return defaultOrderAmount;
    }

    public void setDefaultOrderAmount(double defaultOrderAmount) {
        this.defaultOrderAmount = defaultOrderAmount;
    }

    public boolean isShareAnalytics() {
        return shareAnalytics;
    }

    public void setShareAnalytics(boolean shareAnalytics) {
        this.shareAnalytics = shareAnalytics;
    }

    public boolean isMarketingEmails() {
        return marketingEmails;
    }

    public void setMarketingEmails(boolean marketingEmails) {
        this.marketingEmails = marketingEmails;
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