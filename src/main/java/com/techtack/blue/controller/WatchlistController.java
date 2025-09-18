package com.techtack.blue.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techtack.blue.dto.StockDto;
import com.techtack.blue.dto.WatchlistDto;
import com.techtack.blue.exception.UserException;
import com.techtack.blue.service.WatchlistService;
import com.techtack.blue.service.NotificationService;
import java.util.Map;

@RestController
@RequestMapping("/watchlists")
public class WatchlistController {

    @Autowired
    private WatchlistService watchlistService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<WatchlistDto> createWatchlist(
            @RequestBody WatchlistDto watchlistDto,
            @RequestParam("userId") Long userId) throws UserException {
        WatchlistDto createdWatchlist = watchlistService.createWatchlist(watchlistDto, userId);

        try {
            String deviceToken = "user_device_token_" + userId;
            notificationService.sendNotificationToDevice(
                    deviceToken,
                    "Watchlist Created",
                    String.format("Your watchlist '%s' has been created successfully.", createdWatchlist.getName()),
                    Map.of(
                            "type", "watchlist_created",
                            "watchlist_id", createdWatchlist.getId().toString(),
                            "watchlist_name", createdWatchlist.getName()
                    )
            );
        } catch (Exception e) {
            System.err.println("Failed to send watchlist creation notification: " + e.getMessage());
        }

        return ResponseEntity.ok(createdWatchlist);
    }

    @GetMapping
    public ResponseEntity<List<WatchlistDto>> getUserWatchlists(
            @RequestParam("userId") Long userId) {
        return ResponseEntity.ok(watchlistService.getUserWatchlists(userId));
    }

    @GetMapping("/{watchlistId}")
    public ResponseEntity<WatchlistDto> getWatchlistById(
            @PathVariable Long watchlistId,
            @RequestParam("userId") Long userId) {
        return ResponseEntity.ok(watchlistService.getWatchlistById(watchlistId, userId));
    }

    @PutMapping("/{watchlistId}")
    public ResponseEntity<WatchlistDto> updateWatchlist(
            @PathVariable Long watchlistId,
            @RequestBody WatchlistDto watchlistDto,
            @RequestParam("userId") Long userId) throws UserException {
        return ResponseEntity.ok(watchlistService.updateWatchlist(watchlistId, watchlistDto, userId));
    }

    @DeleteMapping("/{watchlistId}")
    public ResponseEntity<Void> deleteWatchlist(
            @PathVariable Long watchlistId,
            @RequestParam("userId") Long userId) {
        watchlistService.deleteWatchlist(watchlistId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{watchlistId}/stocks")
    public ResponseEntity<WatchlistDto> addStockToWatchlist(
            @PathVariable Long watchlistId,
            @RequestParam Long stockId,
            @RequestParam Long userId) {
        WatchlistDto updatedWatchlist = watchlistService.addStockToWatchlistById(watchlistId, stockId, userId);

        try {
            String deviceToken = "user_device_token_" + userId;
            notificationService.sendNotificationToDevice(
                    deviceToken,
                    "Stock Added to Watchlist",
                    String.format("A stock has been added to your watchlist '%s'.", updatedWatchlist.getName()),
                    Map.of(
                            "type", "stock_added_to_watchlist",
                            "watchlist_id", watchlistId.toString(),
                            "stock_id", stockId.toString()
                    )
            );
        } catch (Exception e) {
            System.err.println("Failed to send stock addition notification: " + e.getMessage());
        }

        return ResponseEntity.ok(updatedWatchlist);
    }

    @DeleteMapping("/{watchlistId}/stocks/{stockId}")
    public ResponseEntity<Void> removeStockFromWatchlist(
            @PathVariable Long watchlistId,
            @PathVariable Long stockId,
            @RequestParam Long userId) {
        watchlistService.removeStockFromWatchlistById(watchlistId, stockId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{watchlistId}/stocks")
    public ResponseEntity<List<StockDto>> getWatchlistStocks(
            @PathVariable Long watchlistId,
            @RequestParam Long userId) {
        return ResponseEntity.ok(watchlistService.getWatchlistStocks(watchlistId, userId));
    }
}