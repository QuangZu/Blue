package com.techtack.blue.service;

import java.util.List;
import java.util.stream.Collectors;

import com.techtack.blue.service.stock.StockService;
import com.techtack.blue.model.Stock;
import com.techtack.blue.dto.stock.ThongTinCoBanDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.techtack.blue.dto.StockDto;
import com.techtack.blue.dto.WatchlistDto;
import com.techtack.blue.exception.UserException;
import com.techtack.blue.model.User;
import com.techtack.blue.model.Watchlist;
import com.techtack.blue.repository.StockRepository;
import com.techtack.blue.repository.UserRepository;
import com.techtack.blue.repository.WatchlistRepository;

@Service
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;

    private final UserRepository userRepository;

    private final StockRepository stockRepository;
    private final StockService stockService;

    @Autowired
    public WatchlistService(WatchlistRepository watchlistRepository, UserRepository userRepository, StockRepository stockRepository, StockService stockService) {
        this.watchlistRepository = watchlistRepository;
        this.userRepository = userRepository;
        this.stockRepository = stockRepository;
        this.stockService = stockService;
    }

    @Transactional
    public WatchlistDto createWatchlist(WatchlistDto watchlistDto, Long userId) throws UserException {
        User user = validateAndGetUser(userId);

        if (watchlistRepository.existsByNameAndUser(watchlistDto.getName(), user)) {
            throw new UserException("Watchlist with name '" + watchlistDto.getName() + "' already exists");
        }

        Watchlist watchlist = new Watchlist();
        watchlist.setName(watchlistDto.getName());
        watchlist.setUser(user);
        watchlist = watchlistRepository.save(watchlist);
        return convertToDto(watchlist);
    }

    @Transactional
    public WatchlistDto addStockToWatchlist(Long watchlistId, String symbol, Long userId) {
        Watchlist watchlist = getWatchlistWithPermissionCheck(watchlistId, userId);
        Stock stock = validateAndGetStockBySymbol(symbol);
        return addStockToWatchlistInternal(watchlist, stock);
    }

    @Transactional
    public WatchlistDto addStockToWatchlistById(Long watchlistId, Long stockId, Long userId) {
        Watchlist watchlist = getWatchlistWithPermissionCheck(watchlistId, userId);
        Stock stock = validateAndGetStockById(stockId);
        return addStockToWatchlistInternal(watchlist, stock);
    }

    @Transactional
    public WatchlistDto removeStockFromWatchlist(Long watchlistId, String symbol, Long userId) {
        Watchlist watchlist = getWatchlistWithPermissionCheck(watchlistId, userId);
        Stock stock = stockRepository.findByCode(symbol).orElse(null);
        return removeStockFromWatchlistInternal(watchlist, stock, "symbol: " + symbol);
    }

    @Transactional
    public WatchlistDto removeStockFromWatchlistById(Long watchlistId, Long stockId, Long userId) {
        Watchlist watchlist = getWatchlistWithPermissionCheck(watchlistId, userId);
        Stock stock = validateAndGetStockById(stockId);
        return removeStockFromWatchlistInternal(watchlist, stock, "id: " + stockId);
    }

    public List<StockDto> getWatchlistStocks(Long watchlistId, Long userId) {
        Watchlist watchlist = getWatchlistWithPermissionCheck(watchlistId, userId);
        return convertStockListToDto(watchlist.getStocks());
    }

    public List<WatchlistDto> getUserWatchlists(Long userId) {
        User user = validateAndGetUser(userId);
        List<Watchlist> watchlists = watchlistRepository.findByUser(user);
        return watchlists.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public WatchlistDto getWatchlistById(Long watchlistId, Long userId) {
        Watchlist watchlist = getWatchlistWithPermissionCheck(watchlistId, userId);
        return convertToDto(watchlist);
    }

    @Transactional
    public WatchlistDto updateWatchlist(Long watchlistId, WatchlistDto watchlistDto, Long userId) throws UserException {
        Watchlist watchlist = getWatchlistWithPermissionCheck(watchlistId, userId);

        if (!watchlist.getName().equals(watchlistDto.getName()) &&
                watchlistRepository.existsByNameAndUser(watchlistDto.getName(), watchlist.getUser())) {
            throw new UserException("Watchlist with name '" + watchlistDto.getName() + "' already exists");
        }

        watchlist.setName(watchlistDto.getName());
        watchlist = watchlistRepository.save(watchlist);

        return convertToDto(watchlist);
    }

    @Transactional
    public void deleteWatchlist(Long watchlistId, Long userId) {
        Watchlist watchlist = getWatchlistWithPermissionCheck(watchlistId, userId);
        watchlistRepository.delete(watchlist);
    }

    private User validateAndGetUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found with id: " + userId
                ));
    }

    private Stock validateAndGetStockBySymbol(String symbol) {
        Stock stock = stockRepository.findByCode(symbol).orElse(null);
        if (stock == null) {
            ThongTinCoBanDto basicInfo = stockService.getStockBasic(symbol);
            if (basicInfo == null) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Stock not found with symbol: " + symbol
                );
            }
            stock = stockRepository.findByCode(basicInfo.getMack()).orElse(null);
            if (stock == null) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve or save stock data after fetching basic info");
            }
        }
        return stock;
    }

    private Stock validateAndGetStockById(Long stockId) {
        return stockRepository.findById(stockId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Stock not found with id: " + stockId
                ));
    }

    private WatchlistDto addStockToWatchlistInternal(Watchlist watchlist, Stock stock) {
        if (watchlist.containsStock(stock)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Stock already exists in the watchlist"
            );
        }

        watchlist.addStock(stock);
        watchlist = watchlistRepository.save(watchlist);
        return convertToDto(watchlist);
    }

    private WatchlistDto removeStockFromWatchlistInternal(Watchlist watchlist, Stock stock, String identifier) {
        if (stock == null || !watchlist.containsStock(stock)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Stock not found in watchlist with " + identifier
            );
        }

        watchlist.removeStock(stock);
        watchlist = watchlistRepository.save(watchlist);
        return convertToDto(watchlist);
    }

    private Watchlist getWatchlistWithPermissionCheck(Long watchlistId, Long userId) {
        Watchlist watchlist = watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Watchlist not found with id: " + watchlistId
                ));

        if (!watchlist.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You don't have permission to access this watchlist"
            );
        }

        return watchlist;
    }

    private List<StockDto> convertStockListToDto(List<Stock> stocks) {
        return stocks.stream()
                .map(stockService::convertToDto)
                .collect(Collectors.toList());
    }

    private WatchlistDto convertToDto(Watchlist watchlist) {
        WatchlistDto dto = new WatchlistDto();
        dto.setId(watchlist.getId());
        dto.setName(watchlist.getName());
        dto.setUserId(watchlist.getUser().getId());
        dto.setCreatedAt(watchlist.getCreatedAt());
        dto.setStocks(convertStockListToDto(watchlist.getStocks()));
        return dto;
    }
}