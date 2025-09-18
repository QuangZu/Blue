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
}