package com.techtack.blue.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techtack.blue.model.Order;
import com.techtack.blue.model.Trading;
import com.techtack.blue.model.order.OrderSide;
import com.techtack.blue.model.order.OrderStatus;
import com.techtack.blue.model.order.OrderType;

@Repository
public interface TradingRepository extends JpaRepository<Trading, Long> {
    
    // Find by account
    List<Trading> findByAccountId(String accountId);
    
    // Find by order reference
    List<Trading> findByOrder(Order order);
    Optional<Trading> findByOrderId(Long orderId);
    
    // Find by status (using enum)
    List<Trading> findByStatus(OrderStatus status);
    List<Trading> findByAccountIdAndStatus(String accountId, OrderStatus status);
    
    // Find by order type (using enum)
    List<Trading> findByOrderType(OrderType orderType);
    List<Trading> findByAccountIdAndOrderType(String accountId, OrderType orderType);
    
    // Find by order side
    List<Trading> findByOrderSide(OrderSide orderSide);
    List<Trading> findByAccountIdAndOrderSide(String accountId, OrderSide orderSide);
    
    // Find by symbol
    List<Trading> findBySymbol(String symbol);
    List<Trading> findByAccountIdAndSymbol(String accountId, String symbol);
    
    // Complex queries
    List<Trading> findByAccountIdAndStatusAndOrderType(String accountId, OrderStatus status, OrderType orderType);
    
    // Execution status queries
    @Query("SELECT t FROM Trading t WHERE t.accountId = :accountId AND t.executedQuantity IS NOT NULL")
    List<Trading> findExecutedTradingsByAccountId(@Param("accountId") String accountId);
    
    @Query("SELECT t FROM Trading t WHERE t.accountId = :accountId AND t.executedQuantity < t.quantity")
    List<Trading> findPartiallyExecutedTradingsByAccountId(@Param("accountId") String accountId);
}
