package com.techtack.blue.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techtack.blue.model.Order;
import com.techtack.blue.model.order.OrderStatus;
import com.techtack.blue.model.order.OrderType;
import com.techtack.blue.model.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByUser(User user);
    
    List<Order> findByStockId(Long stockId);
    
    List<Order> findByUserAndStockId(User user, Long stockId);
    
    List<Order> findByUserAndStatus(User user, OrderStatus status);
    
    List<Order> findByUserAndOrderType(User user, OrderType orderType);
    
    @Query("SELECT o FROM Order o WHERE o.user = :user AND o.status = :status AND o.orderType = :orderType")
    List<Order> findByUserAndStatusAndOrderType(@Param("user") User user, @Param("status") OrderStatus status, @Param("orderType") OrderType orderType);
}
