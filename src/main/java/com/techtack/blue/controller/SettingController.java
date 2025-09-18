package com.techtack.blue.controller;

import com.techtack.blue.exception.UserException;
import com.techtack.blue.model.Setting;
import com.techtack.blue.service.SettingService;
import com.techtack.blue.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/settings")
public class SettingController {

    @Autowired
    private SettingService settingService;
    
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserSettings(@PathVariable Long userId) {
        try {
            Setting settings = settingService.getUserSettings(userId);
            return ResponseEntity.ok(settings);
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve settings"));
        }
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<?> updateUserSettings(
            @PathVariable Long userId,
            @RequestBody Setting settings) {
        try {
            Setting updatedSettings = settingService.updateUserSettings(userId, settings);
            return ResponseEntity.ok(updatedSettings);
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update settings"));
        }
    }

    @PatchMapping("/user/{userId}/{category}")
    public ResponseEntity<?> updateSettingCategory(
            @PathVariable Long userId,
            @PathVariable String category,
            @RequestBody Map<String, Object> updates) {
        try {
            Setting updatedSettings = settingService.updateSettingCategory(userId, category, updates);
            return ResponseEntity.ok(updatedSettings);
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update " + category + " settings"));
        }
    }

    @PostMapping("/user/{userId}/reset")
    public ResponseEntity<?> resetToDefault(@PathVariable Long userId) {
        try {
            Setting defaultSettings = settingService.resetToDefault(userId);
            return ResponseEntity.ok(defaultSettings);
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to reset settings"));
        }
    }

    /**
     * Get available setting options
     */
    @GetMapping("/options")
    public ResponseEntity<Map<String, Object>> getSettingOptions() {
        Map<String, Object> options = Map.of(
            "themes", new String[]{"LIGHT", "DARK"},
            "languages", new String[]{"EN", "VI", "ES", "FR", "DE"},
            "mainScreens", new String[]{"HOME", "PORTFOLIO", "MARKET", "WATCHLIST"},
            "smartOtpMethods", new String[]{"SMS", "EMAIL", "APP"},
            "orderTypes", new String[]{"MARKET", "LIMIT", "STOP", "STOP_LIMIT"}
        );
        return ResponseEntity.ok(options);
    }

    /**
     * Toggle specific boolean setting
     */
    @PostMapping("/user/{userId}/toggle/{settingName}")
    public ResponseEntity<?> toggleSetting(
            @PathVariable Long userId,
            @PathVariable String settingName) {
        try {
            Setting currentSettings = settingService.getUserSettings(userId);
            
            // Toggle the specific boolean setting
            switch (settingName) {
                case "savePinOtp":
                    currentSettings.setSavePinOtp(!currentSettings.isSavePinOtp());
                    break;
                case "faceIdTouchIdEnabled":
                    currentSettings.setFaceIdTouchIdEnabled(!currentSettings.isFaceIdTouchIdEnabled());
                    break;
                case "twoFactorAuthEnabled":
                    currentSettings.setTwoFactorAuthEnabled(!currentSettings.isTwoFactorAuthEnabled());
                    break;
                case "notificationsEnabled":
                    currentSettings.setNotificationsEnabled(!currentSettings.isNotificationsEnabled());
                    break;
                case "hideIndexChart":
                    currentSettings.setHideIndexChart(!currentSettings.isHideIndexChart());
                    break;
                case "priceAlerts":
                    currentSettings.setPriceAlerts(!currentSettings.isPriceAlerts());
                    break;
                case "orderNotifications":
                    currentSettings.setOrderNotifications(!currentSettings.isOrderNotifications());
                    break;
                case "marketNews":
                    currentSettings.setMarketNews(!currentSettings.isMarketNews());
                    break;
                case "systemNotifications":
                    currentSettings.setSystemNotifications(!currentSettings.isSystemNotifications());
                    break;
                case "confirmOrders":
                    currentSettings.setConfirmOrders(!currentSettings.isConfirmOrders());
                    break;
                case "shareAnalytics":
                    currentSettings.setShareAnalytics(!currentSettings.isShareAnalytics());
                    break;
                case "marketingEmails":
                    currentSettings.setMarketingEmails(!currentSettings.isMarketingEmails());
                    break;
                default:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("error", "Invalid setting name: " + settingName));
            }
            
            Setting updatedSettings = settingService.updateUserSettings(userId, currentSettings);
            return ResponseEntity.ok(updatedSettings);
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to toggle setting"));
        }
    }

    @PostMapping("/notifications/price-alerts/toggle")
    public ResponseEntity<String> togglePriceAlerts(@RequestParam("userId") Long userId) {
        try {
            String result = settingService.togglePriceAlerts(userId);
        
        // Send notification about settings change
        try {
            String deviceToken = "user_device_token_" + userId;
            boolean enabled = result.contains("enabled");
            notificationService.sendNotificationToDevice(
                deviceToken,
                "Settings Updated",
                String.format("Price alerts have been %s.", enabled ? "enabled" : "disabled"),
                Map.of(
                    "type", "settings_change",
                    "setting", "price_alerts",
                    "enabled", String.valueOf(enabled)
                )
            );
        } catch (Exception e) {
            System.err.println("Failed to send settings notification: " + e.getMessage());
        }
        
            return ResponseEntity.ok(result);
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to toggle price alerts");
        }
    }

    @PostMapping("/notifications/order-notifications/toggle")
    public ResponseEntity<String> toggleOrderNotifications(@RequestParam("userId") Long userId) {
        try {
            String result = settingService.toggleOrderNotifications(userId);
        
        // Send notification about settings change
        try {
            String deviceToken = "user_device_token_" + userId;
            boolean enabled = result.contains("enabled");
            notificationService.sendNotificationToDevice(
                deviceToken,
                "Settings Updated",
                String.format("Order notifications have been %s.", enabled ? "enabled" : "disabled"),
                Map.of(
                    "type", "settings_change",
                    "setting", "order_notifications",
                    "enabled", String.valueOf(enabled)
                )
            );
        } catch (Exception e) {
            System.err.println("Failed to send settings notification: " + e.getMessage());
        }
        
            return ResponseEntity.ok(result);
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to toggle order notifications");
        }
    }
}
