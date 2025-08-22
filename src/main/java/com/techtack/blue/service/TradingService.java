package com.techtack.blue.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.techtack.blue.dto.StockDto;
import com.techtack.blue.exception.InsufficientFundsException;
import com.techtack.blue.model.Order;
import com.techtack.blue.model.Stock;
import com.techtack.blue.model.Trading;
import com.techtack.blue.model.User;
import com.techtack.blue.model.order.OrderSide;
import com.techtack.blue.model.order.OrderStatus;
import com.techtack.blue.model.order.OrderType;
import com.techtack.blue.repository.OrderRepository;
import com.techtack.blue.repository.StockRepository;
import com.techtack.blue.repository.TradingRepository;
import com.techtack.blue.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class TradingService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private StockRepository stockRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TradingRepository tradingRepository;

    @Autowired
    private StockService stockService;

    @Transactional
    public Order placeOrder(Order order, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        Stock stock = stockRepository.findBySymbol(order.getSymbol());
        if (stock == null) {
            StockDto stockDto = stockService.getStockBySymbol(order.getSymbol());
            if (stockDto == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Stock not found with symbol: " + order.getSymbol());
            }
            stock = stockRepository.findBySymbol(order.getSymbol());
            if (stock == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save stock data");
            }
        }
        
        order.setUser(user);
        order.setStock(stock);
        order.setSymbol(stock.getSymbol());
        order.setEntryDate(LocalDate.now());
        order.setEntryTime(LocalTime.now());
        
        validateOrderTypeFields(order);
        
        if (order.getOrderSide() == OrderSide.BUY) {
            double totalCost = order.getPrice() * order.getQuantity();
            if (user.getBuyingPower() < totalCost) {
                throw new InsufficientFundsException("Insufficient buying power");
            }
            user.setBuyingPower(user.getBuyingPower() - totalCost);
            userRepository.save(user);
        }
        
        order.setBuyingPower(user.getBuyingPower());
        order.setMaxQuantity(calculateMaxQuantity(user.getBuyingPower(), order.getPrice()));
        
        Order savedOrder = orderRepository.save(order);
        createTradingFromOrder(savedOrder);
        
        return savedOrder;
    }
    
    private void validateOrderTypeFields(Order order) {
        switch (order.getOrderType()) {
            case STOP:
                if (order.getTriggerPrice() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trigger price is required for Stop orders");
                }
                break;
            case STOP_LIMIT:
                if (order.getTriggerPrice() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trigger price is required for Stop Limit orders");
                }
                if (order.getPrice() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Limit price is required for Stop Limit orders");
                }
                break;
            case TRAILING_STOP:
                if (order.getTrailingAmount() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trailing amount is required for Trailing Stop orders");
                }
                break;
            case TRAILING_STOP_LIMIT:
                if (order.getTrailingAmount() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trailing amount is required for Trailing Stop Limit orders");
                }
                if (order.getPrice() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Limit price is required for Trailing Stop Limit orders");
                }
                break;
            case OCO:
                if (order.getTakeProfitPrice() == null || order.getCutLossPrice() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Take profit and cut loss prices are required for OCO orders");
                }
                break;
            case STOP_LOSS_TAKE_PROFIT:
                if (order.getTakeProfitPrice() == null || order.getCutLossPrice() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Take profit and cut loss prices are required for Stop Loss/Take Profit orders");
                }
                break;
            case GTD:
                if (order.getExpiryDate() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Expiry date is required for GTD orders");
                }
                break;
            case NORMAL:
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order type");
        }
    }
    
    private Double calculateMaxQuantity(Double buyingPower, Double price) {
        if (price == null || price <= 0) {
            return 0.0;
        }
        return Math.floor(buyingPower / price);
    }
    
    private void createTradingFromOrder(Order order) {
        Trading trading = new Trading();
        trading.setSymbol(order.getStock().getSymbol());
        trading.setAccountId(order.getUser().getId().toString());
        trading.setOrder(order);
        trading.setQuantity(order.getQuantity().doubleValue());
        trading.setPrice(order.getPrice());
        trading.setTriggerPrice(order.getTriggerPrice());
        trading.setTrailingAmount(order.getTrailingAmount());
        trading.setTakeProfitPrice(order.getTakeProfitPrice());
        trading.setCutLossPrice(order.getCutLossPrice());
        trading.setToler(order.getToler());
        trading.setOrderType(order.getOrderType());
        trading.setStatus(order.getStatus());
        trading.setOrderSide(order.getOrderSide());
        trading.setMarketPrice(order.getMarketPrice());
        trading.setEffectiveDate(order.getEffectiveDate());
        trading.setExpiryDate(order.getExpiryDate());
        trading.setCreatedAt(order.getEntryTime());
        trading.setBuyingPower(order.getBuyingPower());
        trading.setMaxQuantity(order.getMaxQuantity());
        
        tradingRepository.save(trading);
    }
    
    public List<Order> getUserOrders(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return orderRepository.findByUser(user);
    }
    
    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        
        if (!order.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access to order denied");
        }
        
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot cancel a " + order.getStatus() + " order");
        }
        
        if (order.getOrderSide() == OrderSide.BUY) {
            User user = order.getUser();
            user.setBuyingPower(user.getBuyingPower() + (order.getPrice() * order.getQuantity()));
            userRepository.save(user);
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        
        Optional<Trading> tradingOptional = tradingRepository.findByOrderId(orderId);
        if (tradingOptional.isPresent()) {
            Trading trading = tradingOptional.get();
            trading.setStatus(OrderStatus.CANCELLED);
            tradingRepository.save(trading);
        }
    }
    
    public List<Order> getOpenOrders(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return orderRepository.findByUserAndStatus(user, OrderStatus.PENDING);
    }
    
    public List<Order> getOrdersByStatus(Long userId, OrderStatus status) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return orderRepository.findByUserAndStatus(user, status);
    }
    
    public List<Order> getOrdersByType(Long userId, OrderType orderType) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return orderRepository.findByUserAndOrderType(user, orderType);
    }
    
    public List<Order> getOrdersByStatusAndType(Long userId, OrderStatus status, OrderType orderType) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return orderRepository.findByUserAndStatusAndOrderType(user, status, orderType);
    }
}
