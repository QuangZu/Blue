package com.techtack.blue.repository;

import com.techtack.blue.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByCode(String code);

    @Query("SELECT s FROM Stock s ORDER BY s.volume DESC")
    List<Stock> findTopTradedStocks();

    @Query("SELECT s FROM Stock s WHERE s.previousClose IS NOT NULL AND s.previousClose != 0 AND (s.price - s.previousClose) / s.previousClose > 0 ORDER BY (s.price - s.previousClose) / s.previousClose DESC")
    List<Stock> findTopGainers();

    @Query("SELECT s FROM Stock s WHERE s.previousClose IS NOT NULL AND s.previousClose != 0 AND (s.price - s.previousClose) / s.previousClose < 0 ORDER BY (s.price - s.previousClose) / s.previousClose ASC")
    List<Stock> findTopLosers();

    @Query("SELECT s FROM Stock s WHERE s.marketCap BETWEEN :minMarketCap AND :maxMarketCap ORDER BY s.marketCap DESC")
    List<Stock> findByMarketCapRange(@Param("minMarketCap") double minMarketCap, @Param("maxMarketCap") double maxMarketCap);

    List<Stock> findByExchange(String exchange);

    @Query("SELECT s FROM Stock s WHERE s.isActive = true ORDER BY s.code")
    List<Stock> findAllActive();

    @Query("SELECT s FROM Stock s WHERE s.isActive = true AND s.exchange = :exchange ORDER BY s.code")
    List<Stock> findActiveByExchange(@Param("exchange") String exchange);

    @Query("SELECT s FROM Stock s WHERE LOWER(s.code) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Stock> searchStocks(@Param("keyword") String keyword);
}
