package com.techtack.blue.service;

import com.google.firebase.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    public String sendNotificationToDevice(String deviceToken, String title, String body, Map<String, String> data) {
        try {
            Message.Builder messageBuilder = Message.builder()
                .setToken(deviceToken)
                .setNotification(Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build());
            
            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }
            
            Message message = messageBuilder.build();
            String response = FirebaseMessaging.getInstance().send(message);
            
            logger.info("[NOTIFICATION SERVICE] Successfully sent message to device {}: {}", deviceToken, response);
            return response;
            
        } catch (FirebaseMessagingException e) {
            logger.error("[NOTIFICATION SERVICE] Failed to send notification to device {}: {}", deviceToken, e.getMessage());
            return null;
        }
    }

    public BatchResponse sendNotificationToMultipleDevices(List<String> deviceTokens, String title, String body, Map<String, String> data) {
        try {
            MulticastMessage.Builder messageBuilder = MulticastMessage.builder()
                .addAllTokens(deviceTokens)
                .setNotification(Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build());
            
            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }
            
            MulticastMessage message = messageBuilder.build();
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
            
            logger.info("[NOTIFICATION SERVICE] Sent multicast message. Success count: {}, Failure count: {}", 
                response.getSuccessCount(), response.getFailureCount());
            
            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        logger.warn("[NOTIFICATION SERVICE] Failed to send to token {}: {}", 
                            deviceTokens.get(i), responses.get(i).getException().getMessage());
                    }
                }
            }
            
            return response;
            
        } catch (FirebaseMessagingException e) {
            logger.error("[NOTIFICATION SERVICE] Failed to send multicast notification: {}", e.getMessage());
            return null;
        }
    }
    
    public String sendNotificationToTopic(String topic, String title, String body, Map<String, String> data) {
        try {
            Message.Builder messageBuilder = Message.builder()
                .setTopic(topic)
                .setNotification(Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build());
            
            if (data != null && !data.isEmpty()) {
                messageBuilder.putAllData(data);
            }
            
            Message message = messageBuilder.build();
            String response = FirebaseMessaging.getInstance().send(message);
            
            logger.info("[NOTIFICATION SERVICE] Successfully sent message to topic {}: {}", topic, response);
            return response;
            
        } catch (FirebaseMessagingException e) {
            logger.error("[NOTIFICATION SERVICE] Failed to send notification to topic {}: {}", topic, e.getMessage());
            return null;
        }
    }
    
    public boolean subscribeToTopic(String deviceToken, String topic) {
        try {
            FirebaseMessaging.getInstance().subscribeToTopic(List.of(deviceToken), topic);
            logger.info("[NOTIFICATION SERVICE] Successfully subscribed device {} to topic {}", deviceToken, topic);
            return true;
        } catch (FirebaseMessagingException e) {
            logger.error("[NOTIFICATION SERVICE] Failed to subscribe device {} to topic {}: {}", deviceToken, topic, e.getMessage());
            return false;
        }
    }
    
    public boolean unsubscribeFromTopic(String deviceToken, String topic) {
        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(List.of(deviceToken), topic);
            logger.info("[NOTIFICATION SERVICE] Successfully unsubscribed device {} from topic {}", deviceToken, topic);
            return true;
        } catch (FirebaseMessagingException e) {
            logger.error("[NOTIFICATION SERVICE] Failed to unsubscribe device {} from topic {}: {}", deviceToken, topic, e.getMessage());
            return false;
        }
    }
    
    public String sendTradingNotification(String deviceToken, String stockSymbol, String action, int quantity, double price) {
        String title = String.format("%s Order Executed", action);
        String body = String.format("%s %d shares of %s at $%.2f", action, quantity, stockSymbol, price);
        
        Map<String, String> data = Map.of(
            "type", "trading",
            "stock_symbol", stockSymbol,
            "action", action,
            "quantity", String.valueOf(quantity),
            "price", String.valueOf(price)
        );
        
        return sendNotificationToDevice(deviceToken, title, body, data);
    }
    
    public String sendPriceAlertNotification(String deviceToken, String stockSymbol, double currentPrice, double targetPrice, String alertType) {
        String title = String.format("%s Price Alert", stockSymbol);
        String body = String.format("%s is now $%.2f (%s your target of $%.2f)", 
            stockSymbol, currentPrice, alertType.toLowerCase(), targetPrice);
        
        Map<String, String> data = Map.of(
            "type", "price_alert",
            "stock_symbol", stockSymbol,
            "current_price", String.valueOf(currentPrice),
            "target_price", String.valueOf(targetPrice),
            "alert_type", alertType
        );
        
        return sendNotificationToDevice(deviceToken, title, body, data);
    }
}