package com.techtack.blue.service;

import com.techtack.blue.exception.UserException;
import com.techtack.blue.model.Setting;
import com.techtack.blue.model.User;
import com.techtack.blue.repository.SettingRepository;
import com.techtack.blue.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;

@Service
public class SettingService {

    @Autowired
    private SettingRepository settingRepository;

    @Autowired
    private UserRepository userRepository;
    
    private static final List<String> VALID_LANGUAGES = Arrays.asList("EN", "VN");
    private static final List<String> VALID_MAIN_SCREENS = Arrays.asList("Home", "Watch List", "Market", "Trading", "My account");
    
    private static final Map<String, Map<String, BiConsumer<Setting, Object>>> CATEGORY_UPDATERS = new HashMap<>();
    
    static {
        initializeCategoryUpdaters();
    }
    
    public Setting getUserSettings(Long userId) throws UserException {
        User user = validateAndGetUser(userId);
        Setting settings = settingRepository.findByUserId(userId);
        return settings != null ? settings : createDefaultSettings(user);
    }

    public Setting updateUserSettings(Long userId, Setting updatedSettings) throws UserException {
        User user = validateAndGetUser(userId);
        Setting existingSettings = settingRepository.findByUserId(userId);
        
        if (existingSettings == null) {
            updatedSettings.setUser(user);
            validateSettings(updatedSettings);
            return settingRepository.save(updatedSettings);
        }
        
        updateAllSettingsFields(existingSettings, updatedSettings);
        validateSettings(existingSettings);
        return settingRepository.save(existingSettings);
    }
    
    public String togglePriceAlerts(Long userId) throws UserException {
        User user = validateAndGetUser(userId);
        Setting settings = getUserSettings(userId);
        
        boolean newValue = !settings.isPriceAlerts();
        settings.setPriceAlerts(newValue);
        settingRepository.save(settings);
        
        return "Price alerts have been " + (newValue ? "enabled" : "disabled") + " for user " + userId;
    }
    
    public String toggleOrderNotifications(Long userId) throws UserException {
        User user = validateAndGetUser(userId);
        Setting settings = getUserSettings(userId);
        
        boolean newValue = !settings.isOrderNotifications();
        settings.setOrderNotifications(newValue);
        settingRepository.save(settings);
        
        return "Order notifications have been " + (newValue ? "enabled" : "disabled") + " for user " + userId;
    }

    public Setting updateSettingCategory(Long userId, String category, Map<String, Object> updates) throws UserException {
        Setting settings = getUserSettings(userId);
        
        Map<String, BiConsumer<Setting, Object>> categoryUpdaters = CATEGORY_UPDATERS.get(category.toLowerCase());
        if (categoryUpdaters == null) {
            throw new UserException("Invalid setting category: " + category);
        }
        
        updates.forEach((key, value) -> {
            BiConsumer<Setting, Object> updater = categoryUpdaters.get(key);
            if (updater != null) {
                updater.accept(settings, value);
            }
        });
        
        validateSettings(settings);
        return settingRepository.save(settings);
    }

    public Setting resetToDefault(Long userId) throws UserException {
        User user = validateAndGetUser(userId);
        Setting existingSettings = settingRepository.findByUserId(userId);
        
        if (existingSettings != null) {
            settingRepository.delete(existingSettings);
        }
        
        return createDefaultSettings(user);
    }

    private User validateAndGetUser(Long userId) throws UserException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found with id: " + userId));
    }
    
    private void validateSettings(Setting settings) throws UserException {
        if (settings.getLanguage() != null && !VALID_LANGUAGES.contains(settings.getLanguage())) {
            throw new UserException("Invalid language. Must be one of: " + String.join(", ", VALID_LANGUAGES));
        }
        
        if (settings.getMainScreen() != null && !VALID_MAIN_SCREENS.contains(settings.getMainScreen())) {
            throw new UserException("Invalid main screen. Must be one of: " + String.join(", ", VALID_MAIN_SCREENS));
        }
    }

    private Setting createDefaultSettings(User user) {
        Setting defaultSettings = new Setting();
        defaultSettings.setUser(user);
        
        setSecurityDefaults(defaultSettings);
        setApplicationDefaults(defaultSettings);
        setNotificationDefaults(defaultSettings);
        setTradingDefaults(defaultSettings);
        setPrivacyDefaults(defaultSettings);
        
        return settingRepository.save(defaultSettings);
    }
    
    private void setSecurityDefaults(Setting settings) {
        settings.setSavePinOtp(false);
        settings.setFaceIdTouchIdEnabled(false);
        settings.setTwoFactorAuthEnabled(false);
        settings.setSmartOtpMethod("SMS");
    }
    
    private void setApplicationDefaults(Setting settings) {
        settings.setNotificationsEnabled(true);
        settings.setLightTheme(false);
        settings.setMainScreen("Home");
        settings.setLanguage("EN");
        settings.setHideIndexChart(false);
    }
    
    private void setNotificationDefaults(Setting settings) {
        settings.setPriceAlerts(true);
        settings.setOrderNotifications(true);
        settings.setMarketNews(true);
        settings.setSystemNotifications(true);
    }
    
    private void setTradingDefaults(Setting settings) {
        settings.setDefaultOrderType("MARKET");
        settings.setConfirmOrders(true);
        settings.setDefaultOrderAmount(0.0);
    }
    
    private void setPrivacyDefaults(Setting settings) {
        settings.setShareAnalytics(false);
        settings.setMarketingEmails(false);
    }

    private void updateAllSettingsFields(Setting existing, Setting updated) {
        updateSecurityFields(existing, updated);
        updateApplicationFields(existing, updated);
        updateNotificationFields(existing, updated);
        updateTradingFields(existing, updated);
        updatePrivacyFields(existing, updated);
    }
    
    private void updateSecurityFields(Setting existing, Setting updated) {
        existing.setSavePinOtp(updated.isSavePinOtp());
        existing.setFaceIdTouchIdEnabled(updated.isFaceIdTouchIdEnabled());
        existing.setTwoFactorAuthEnabled(updated.isTwoFactorAuthEnabled());
        if (updated.getSmartOtpMethod() != null) {
            existing.setSmartOtpMethod(updated.getSmartOtpMethod());
        }
    }
    
    private void updateApplicationFields(Setting existing, Setting updated) {
        existing.setNotificationsEnabled(updated.isNotificationsEnabled());
        existing.setLightTheme(updated.isLightTheme());
        if (updated.getMainScreen() != null) {
            existing.setMainScreen(updated.getMainScreen());
        }
        if (updated.getLanguage() != null) {
            existing.setLanguage(updated.getLanguage());
        }
        existing.setHideIndexChart(updated.isHideIndexChart());
    }
    
    private void updateNotificationFields(Setting existing, Setting updated) {
        existing.setPriceAlerts(updated.isPriceAlerts());
        existing.setOrderNotifications(updated.isOrderNotifications());
        existing.setMarketNews(updated.isMarketNews());
        existing.setSystemNotifications(updated.isSystemNotifications());
    }
    
    private void updateTradingFields(Setting existing, Setting updated) {
        if (updated.getDefaultOrderType() != null) {
            existing.setDefaultOrderType(updated.getDefaultOrderType());
        }
        existing.setConfirmOrders(updated.isConfirmOrders());
        existing.setDefaultOrderAmount(updated.getDefaultOrderAmount());
    }
    
    private void updatePrivacyFields(Setting existing, Setting updated) {
        existing.setShareAnalytics(updated.isShareAnalytics());
        existing.setMarketingEmails(updated.isMarketingEmails());
    }
    
    private static void initializeCategoryUpdaters() {
        Map<String, BiConsumer<Setting, Object>> securityUpdaters = new HashMap<>();
        securityUpdaters.put("savePinOtp", (s, v) -> s.setSavePinOtp((Boolean) v));
        securityUpdaters.put("faceIdTouchIdEnabled", (s, v) -> s.setFaceIdTouchIdEnabled((Boolean) v));
        securityUpdaters.put("twoFactorAuthEnabled", (s, v) -> s.setTwoFactorAuthEnabled((Boolean) v));
        securityUpdaters.put("smartOtpMethod", (s, v) -> s.setSmartOtpMethod((String) v));
        CATEGORY_UPDATERS.put("security", securityUpdaters);
        
        Map<String, BiConsumer<Setting, Object>> applicationUpdaters = new HashMap<>();
        applicationUpdaters.put("notificationsEnabled", (s, v) -> s.setNotificationsEnabled((Boolean) v));
        applicationUpdaters.put("lightTheme", (s, v) -> s.setLightTheme((Boolean) v));
        applicationUpdaters.put("mainScreen", (s, v) -> {
            String screen = (String) v;
            if (!VALID_MAIN_SCREENS.contains(screen)) {
                throw new IllegalArgumentException("Invalid main screen: " + screen);
            }
            s.setMainScreen(screen);
        });
        applicationUpdaters.put("language", (s, v) -> {
            String lang = (String) v;
            if (!VALID_LANGUAGES.contains(lang)) {
                throw new IllegalArgumentException("Invalid language: " + lang);
            }
            s.setLanguage(lang);
        });
        applicationUpdaters.put("hideIndexChart", (s, v) -> s.setHideIndexChart((Boolean) v));
        CATEGORY_UPDATERS.put("application", applicationUpdaters);
        
        Map<String, BiConsumer<Setting, Object>> notificationUpdaters = new HashMap<>();
        notificationUpdaters.put("priceAlerts", (s, v) -> s.setPriceAlerts((Boolean) v));
        notificationUpdaters.put("orderNotifications", (s, v) -> s.setOrderNotifications((Boolean) v));
        notificationUpdaters.put("marketNews", (s, v) -> s.setMarketNews((Boolean) v));
        notificationUpdaters.put("systemNotifications", (s, v) -> s.setSystemNotifications((Boolean) v));
        CATEGORY_UPDATERS.put("notification", notificationUpdaters);
        
        Map<String, BiConsumer<Setting, Object>> tradingUpdaters = new HashMap<>();
        tradingUpdaters.put("defaultOrderType", (s, v) -> s.setDefaultOrderType((String) v));
        tradingUpdaters.put("confirmOrders", (s, v) -> s.setConfirmOrders((Boolean) v));
        tradingUpdaters.put("defaultOrderAmount", (s, v) -> s.setDefaultOrderAmount((Double) v));
        CATEGORY_UPDATERS.put("trading", tradingUpdaters);
        
        Map<String, BiConsumer<Setting, Object>> privacyUpdaters = new HashMap<>();
        privacyUpdaters.put("shareAnalytics", (s, v) -> s.setShareAnalytics((Boolean) v));
        privacyUpdaters.put("marketingEmails", (s, v) -> s.setMarketingEmails((Boolean) v));
        CATEGORY_UPDATERS.put("privacy", privacyUpdaters);
    }
}
