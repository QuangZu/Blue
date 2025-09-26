package com.techtack.blue.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techtack.blue.model.Order;
import com.techtack.blue.model.Trading;
import com.techtack.blue.model.order.OrderSide;
import com.techtack.blue.model.order.OrderStatus;
import com.techtack.blue.model.order.OrderType;
import com.techtack.blue.repository.TradingRepository;
import com.techtack.blue.service.NotificationService;
import com.techtack.blue.service.TradingService;

@RestController
@RequestMapping("/trading")
public class TradingController {

    @Autowired
    private TradingService tradingService;
    
    @Autowired
    private TradingRepository tradingRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @PostMapping("/orders")
    public ResponseEntity<Order> placeOrder(@RequestBody Order order, @RequestParam("userId") Long userId) {
        if (order.getOrderType() == null) {
            order.setOrderType(OrderType.NORMAL);
        }
        Order placedOrder = tradingService.placeOrder(order, userId);
        
        try {
            String deviceToken = "user_device_token_" + userId;
            String action = placedOrder.getOrderSide().getDisplayName();
            notificationService.sendTradingNotification(
                deviceToken,
                placedOrder.getCode(),
                action,
                placedOrder.getQuantity(),
                placedOrder.getPrice() != null ? placedOrder.getPrice() : placedOrder.getMarketPrice()
            );
        } catch (Exception e) {
            System.err.println("Failed to send order notification: " + e.getMessage());
        }
        
        return ResponseEntity.ok(placedOrder);
    }

    @PostMapping("/orders/{orderType}")
    public ResponseEntity<Order> placeOrderByType(
            @RequestBody Order order, 
            @RequestParam("userId") Long userId,
            @PathVariable String orderType) {
        try {
            OrderType type = OrderType.valueOf(orderType.toUpperCase().replace("-", "_"));
            order.setOrderType(type);
            Order placedOrder = tradingService.placeOrder(order, userId);
            
            try {
                String deviceToken = "user_device_token_" + userId;
                String action = placedOrder.getOrderSide().getDisplayName();
                notificationService.sendTradingNotification(
                    deviceToken,
                    placedOrder.getCode(),
                    action,
                    placedOrder.getQuantity(),
                    placedOrder.getPrice() != null ? placedOrder.getPrice() : placedOrder.getMarketPrice()
                );
            } catch (Exception e) {
                System.err.println("Failed to send order notification: " + e.getMessage());
            }
            
            return ResponseEntity.ok(placedOrder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getUserOrders(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(tradingService.getUserOrders(userId));
    }

    @GetMapping("/orders/open")
    public ResponseEntity<List<Order>> getOpenOrders(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(tradingService.getOrdersByStatus(userId, OrderStatus.PENDING));
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<Order> getOrderById(
            @RequestParam("userId") Long userId,
            @PathVariable Long orderId) {
        return tradingService.getUserOrders(userId).stream()
                .filter(order -> order.getId().equals(orderId))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/orders/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(
            @RequestParam("userId") Long userId,
            @PathVariable String status) {
        return getOrdersByEnumValue(userId, status, 
            s -> tradingService.getOrdersByStatus(userId, OrderStatus.valueOf(s.toUpperCase())));
    }

    @GetMapping("/orders/type/{type}")
    public ResponseEntity<List<Order>> getOrdersByType(
            @RequestParam("userId") Long userId,
            @PathVariable String type) {
        return getOrdersByEnumValue(userId, type,
            t -> tradingService.getOrdersByType(userId, OrderType.valueOf(t.toUpperCase())));
    }

    @GetMapping("/orders/status/{status}/type/{type}")
    public ResponseEntity<List<Order>> getOrdersByStatusAndType(
            @RequestParam("userId") Long userId,
            @PathVariable String status,
            @PathVariable String type) {
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            OrderType orderType = OrderType.valueOf(type.toUpperCase());
            List<Order> filteredOrders = tradingService.getOrdersByStatusAndType(userId, orderStatus, orderType);
            return ResponseEntity.ok(filteredOrders);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/orders/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @RequestParam("userId") Long userId,
            @PathVariable Long orderId) {
        
        Order orderToCancel = tradingService.getUserOrders(userId).stream()
                .filter(order -> order.getId().equals(orderId))
                .findFirst()
                .orElse(null);
        
        tradingService.cancelOrder(orderId, userId);
        
        if (orderToCancel != null) {
            try {
                String deviceToken = "user_device_token_" + userId;
                notificationService.sendNotificationToDevice(
                    deviceToken,
                    "Order Cancelled",
                    String.format("Your %s order for %d shares of %s has been cancelled",
                        orderToCancel.getOrderSide().getDisplayName(),
                        orderToCancel.getQuantity(),
                        orderToCancel.getCode()),
                    Map.of(
                        "type", "order_cancellation",
                        "order_id", orderId.toString(),
                        "stock_symbol", orderToCancel.getCode()
                    )
                );
            } catch (Exception e) {
                System.err.println("Failed to send cancellation notification: " + e.getMessage());
            }
        }
        
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/order-types")
    public ResponseEntity<List<Map<String, String>>> getOrderTypes() {
        return ResponseEntity.ok(
            Arrays.stream(OrderType.values())
                .map(type -> Map.of(
                    "code", type.name(),
                    "name", type.getDisplayName()
                ))
                .toList()
        );
    }

    @GetMapping("/order-sides")
    public ResponseEntity<List<OrderSide>> getOrderSides() {
        return ResponseEntity.ok(Arrays.asList(OrderSide.values()));
    }

    @GetMapping("/order-statuses")
    public ResponseEntity<List<OrderStatus>> getOrderStatuses() {
        return ResponseEntity.ok(Arrays.asList(OrderStatus.values()));
    }

    @GetMapping("/tradings")
    public ResponseEntity<List<Trading>> getTradingRecords(@RequestParam("accountId") String accountId) {
        return ResponseEntity.ok(tradingRepository.findByAccountId(accountId));
    }

    @GetMapping("/tradings/code/{code}")
    public ResponseEntity<List<Trading>> getTradingRecordsByCode(
            @RequestParam("accountId") String accountId,
            @PathVariable String code) {
        return ResponseEntity.ok(tradingRepository.findByAccountIdAndCode(accountId, code));
    }

    @GetMapping("/tradings/type/{orderType}")
    public ResponseEntity<List<Trading>> getTradingRecordsByOrderType(
            @RequestParam("accountId") String accountId,
            @PathVariable String orderType) {
        return getTradingRecordsByEnumValue(accountId, orderType,
            type -> tradingRepository.findByAccountIdAndOrderType(accountId, OrderType.valueOf(type.toUpperCase())));
    }

    @GetMapping("/tradings/status/{status}")
    public ResponseEntity<List<Trading>> getTradingRecordsByStatus(
            @RequestParam("accountId") String accountId,
            @PathVariable String status) {
        return getTradingRecordsByEnumValue(accountId, status,
            s -> tradingRepository.findByAccountIdAndStatus(accountId, OrderStatus.valueOf(s.toUpperCase())));
    }

    private ResponseEntity<List<Order>> getOrdersByEnumValue(
            Long userId, String enumValue, 
            java.util.function.Function<String, List<Order>> serviceCall) {
        try {
            List<Order> orders = serviceCall.apply(enumValue);
            return ResponseEntity.ok(orders);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private ResponseEntity<List<Trading>> getTradingRecordsByEnumValue(
            String accountId, String enumValue,
            java.util.function.Function<String, List<Trading>> repositoryCall) {
        try {
            List<Trading> tradings = repositoryCall.apply(enumValue);
            return ResponseEntity.ok(tradings);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
