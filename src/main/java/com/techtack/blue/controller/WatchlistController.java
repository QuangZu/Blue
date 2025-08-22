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

@RestController
@RequestMapping("/watchlists")
public class WatchlistController {

    @Autowired
    private WatchlistService watchlistService;

    @PostMapping
    public ResponseEntity<WatchlistDto> createWatchlist(
            @RequestBody WatchlistDto watchlistDto,
            @RequestParam("userId") Long userId) throws UserException {
        return ResponseEntity.ok(watchlistService.createWatchlist(watchlistDto, userId));
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
        return ResponseEntity.ok(watchlistService.addStockToWatchlistById(watchlistId, stockId, userId));
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
