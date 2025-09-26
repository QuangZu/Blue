package com.techtack.blue.repository.market;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.techtack.blue.model.market.MarketData;

@Repository
public interface MarketDataRepository extends JpaRepository<MarketData, Long> {

    @Query("SELECT md FROM MarketData md " +
           "WHERE md.stock.code = :code " +
           "ORDER BY md.tradingTime DESC")
    Optional<MarketData> findLatestBySymbol(@Param("code") String code);
    
    @Query("SELECT md FROM MarketData md " +
           "WHERE md.stock.san = :exchange " +
           "AND md.tradingTime = (SELECT MAX(md2.tradingTime) FROM MarketData md2 WHERE md2.stock = md.stock)")
    List<MarketData> findLatestByExchange(@Param("exchange") String exchange);
    
    // Note: This query is commented out because Symbol entity's relationship to Industry needs to be properly configured
    // @Query("SELECT md FROM MarketData md " +
    //        "WHERE md.stock.industry.id = :industryId " +
    //        "AND md.tradingTime = (SELECT MAX(md2.tradingTime) FROM MarketData md2 WHERE md2.stock = md.stock)")
    // List<MarketData> findByIndustryId(@Param("industryId") Long industryId);
    
    default List<MarketData> findByIndustryId(Long industryId) {
        return new java.util.ArrayList<>();
    }
    
    @Query("SELECT md FROM MarketData md " +
           "WHERE md.changePercent > 0 " +
           "AND (:exchange IS NULL OR md.stock.san = :exchange) " +
           "AND md.tradingTime = (SELECT MAX(md2.tradingTime) FROM MarketData md2 WHERE md2.stock = md.stock) " +
           "ORDER BY md.changePercent DESC")
    List<MarketData> findTopGainers(@Param("exchange") String exchange, Pageable pageable);
    
    @Query("SELECT md FROM MarketData md " +
           "WHERE md.changePercent < 0 " +
           "AND (:exchange IS NULL OR md.stock.san = :exchange) " +
           "AND md.tradingTime = (SELECT MAX(md2.tradingTime) FROM MarketData md2 WHERE md2.stock = md.stock) " +
           "ORDER BY md.changePercent ASC")
    List<MarketData> findTopLosers(@Param("exchange") String exchange, Pageable pageable);
    
    @Query("SELECT md FROM MarketData md " +
           "WHERE (:exchange IS NULL OR md.stock.san = :exchange) " +
           "AND md.tradingTime = (SELECT MAX(md2.tradingTime) FROM MarketData md2 WHERE md2.stock = md.stock) " +
           "ORDER BY md.volume DESC")
    List<MarketData> findTopVolume(@Param("exchange") String exchange, Pageable pageable);
    
    @Query("SELECT md FROM MarketData md " +
           "WHERE (:exchange IS NULL OR md.stock.san = :exchange) " +
           "AND md.tradingTime = (SELECT MAX(md2.tradingTime) FROM MarketData md2 WHERE md2.stock = md.stock) " +
           "ORDER BY md.value DESC")
    List<MarketData> findTopValue(@Param("exchange") String exchange, Pageable pageable);
    
    @Query("SELECT md FROM MarketData md " +
           "WHERE md.foreignNetValue > 0 " +
           "AND (:exchange IS NULL OR md.stock.san = :exchange) " +
           "AND md.tradingTime = (SELECT MAX(md2.tradingTime) FROM MarketData md2 WHERE md2.stock = md.stock) " +
           "ORDER BY md.foreignNetValue DESC")
    List<MarketData> findTopForeignBuy(@Param("exchange") String exchange, Pageable pageable);
    
    @Query("SELECT md FROM MarketData md " +
           "WHERE md.foreignNetValue < 0 " +
           "AND (:exchange IS NULL OR md.stock.san = :exchange) " +
           "AND md.tradingTime = (SELECT MAX(md2.tradingTime) FROM MarketData md2 WHERE md2.stock = md.stock) " +
           "ORDER BY md.foreignNetValue ASC")
    List<MarketData> findTopForeignSell(@Param("exchange") String exchange, Pageable pageable);
    
    @Query("SELECT SUM(md.value) FROM MarketData md " +
           "WHERE md.stock.san = :exchange " +
           "AND DATE(md.tradingTime) = :date")
    BigDecimal calculateTotalMarketValue(@Param("exchange") String exchange, @Param("date") LocalDate date);
    
    @Query("SELECT AVG(dailyTotal) FROM " +
           "(SELECT DATE(md.tradingTime) as tradingDate, SUM(md.value) as dailyTotal " +
           "FROM MarketData md " +
           "WHERE DATE(md.tradingTime) BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(md.tradingTime)) as dailyLiquidity")
    BigDecimal calculateAverageLiquidity(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT md FROM MarketData md " +
           "WHERE md.currentPrice = md.ceilingPrice " +
           "AND (:exchange IS NULL OR md.stock.san = :exchange) " +
           "AND md.tradingTime = (SELECT MAX(md2.tradingTime) FROM MarketData md2 WHERE md2.stock = md.stock)")
    List<MarketData> findSymbolsAtCeiling(@Param("exchange") String exchange);
    
    @Query("SELECT md FROM MarketData md " +
           "WHERE md.currentPrice = md.floorPrice " +
           "AND (:exchange IS NULL OR md.stock.san = :exchange) " +
           "AND md.tradingTime = (SELECT MAX(md2.tradingTime) FROM MarketData md2 WHERE md2.stock = md.stock)")
    List<MarketData> findSymbolsAtFloor(@Param("exchange") String exchange);
    
    @Query("SELECT md FROM MarketData md " +
           "WHERE md.stock.code = :code " +
           "AND md.tradingTime BETWEEN :startTime AND :endTime " +
           "ORDER BY md.tradingTime ASC")
    List<MarketData> findHistoricalData(@Param("code") String code, 
                                        @Param("startTime") LocalDateTime startTime, 
                                        @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT md.stock.industry.industryCode, AVG(md.changePercent) " +
           "FROM MarketData md " +
           "WHERE md.stock.industry IS NOT NULL " +
           "AND md.tradingTime = (SELECT MAX(md2.tradingTime) FROM MarketData md2 WHERE md2.stock = md.stock) " +
           "GROUP BY md.stock.industry.industryCode")
    List<Object[]> calculateSectorPerformance();
    
    @Query("SELECT md FROM MarketData md " +
           "WHERE (:exchange IS NULL OR md.stock.san = :exchange) " +
           "AND md.tradingTime = (SELECT MAX(md2.tradingTime) FROM MarketData md2 WHERE md2.stock = md.stock) " +
           "ORDER BY (md.volume / NULLIF(md.stock.soluongluuhanh, 0)) DESC")
    List<MarketData> findMostActiveSymbols(@Param("exchange") String exchange, Pageable pageable);
}
