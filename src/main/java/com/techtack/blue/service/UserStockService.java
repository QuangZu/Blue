package com.techtack.blue.service;

import com.techtack.blue.dto.UserStockDto;
import com.techtack.blue.exception.UserException;
import com.techtack.blue.model.Stock;
import com.techtack.blue.model.User;
import com.techtack.blue.model.UserStock;
import com.techtack.blue.repository.StockRepository;
import com.techtack.blue.repository.UserRepository;
import com.techtack.blue.repository.UserStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserStockService {

    @Autowired
    private UserStockRepository userStockRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StockRepository stockRepository;
    
    @Autowired
    private StockService stockService;

    public List<UserStockDto> getUserStocks(Long userId) throws UserException{
        User user = validateAndGetUser(userId);
        List<UserStock> userStocks = userStockRepository.findByUser(user);
        return userStocks.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public UserStockDto buyStock(Long userId, String symbol, int quantity) throws UserException {
        User user = validateAndGetUser(userId);
        Stock stock = validateAndGetStock(symbol);
        
        double totalCost = stock.getPrice() * quantity;
        
        if (user.getAccountBalance() < totalCost) {
            throw new UserException("Insufficient funds to buy stock");
        }
        
        updateUserBalance(user, -totalCost);
        
        UserStock existingUserStock = userStockRepository.findByUserAndStock(user, stock);
        
        if (existingUserStock != null) {
            return updateExistingPosition(existingUserStock, quantity, totalCost);
        } else {
            return createNewPosition(user, stock, quantity);
        }
    }

    public UserStockDto sellStock(Long userId, String symbol, int quantity) throws UserException {
        User user = validateAndGetUser(userId);
        Stock stock = validateAndGetStock(symbol);
        
        UserStock userStock = userStockRepository.findByUserAndStock(user, stock);
        
        if (userStock == null || userStock.getQuantity() < quantity) {
            throw new UserException("Insufficient shares to sell");
        }
        
        double saleProceeds = stock.getPrice() * quantity;
        updateUserBalance(user, saleProceeds);
        
        if (userStock.getQuantity() == quantity) {
            userStockRepository.delete(userStock);
            return null;
        } else {
            userStock.setQuantity(userStock.getQuantity() - quantity);
            userStockRepository.save(userStock);
            return convertToDto(userStock);
        }
    }

    private User validateAndGetUser(Long userId) throws UserException{
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found with id: " + userId));
    }

    private Stock validateAndGetStock(String symbol) throws UserException{
        Stock stock = stockRepository.findBySymbol(symbol);
        if (stock == null) {
            throw new UserException("Stock not found with symbol: " + symbol);
        }
        return stock;
    }

    private void updateUserBalance(User user, double amount) {
        user.setAccountBalance(user.getAccountBalance() + amount);
        userRepository.save(user);
    }

    private UserStockDto updateExistingPosition(UserStock existingUserStock, int quantity, double totalCost) {
        double newTotalCost = (existingUserStock.getPurchasePrice() * existingUserStock.getQuantity()) + totalCost;
        int newTotalQuantity = existingUserStock.getQuantity() + quantity;
        double newAveragePrice = newTotalCost / newTotalQuantity;
        
        existingUserStock.setQuantity(newTotalQuantity);
        existingUserStock.setPurchasePrice(newAveragePrice);
        userStockRepository.save(existingUserStock);
        
        return convertToDto(existingUserStock);
    }

    private UserStockDto createNewPosition(User user, Stock stock, int quantity) {
        UserStock userStock = new UserStock();
        userStock.setUser(user);
        userStock.setStock(stock);
        userStock.setQuantity(quantity);
        userStock.setPurchasePrice(stock.getPrice());
        userStock.setPurchaseDate(LocalDateTime.now());
        
        userStockRepository.save(userStock);
        return convertToDto(userStock);
    }

    private UserStockDto convertToDto(UserStock userStock) {
        UserStockDto dto = new UserStockDto();
        dto.setId(userStock.getId());
        dto.setUserId(userStock.getUser().getId());
        dto.setStock(stockService.getStockBySymbol(userStock.getStock().getSymbol()));
        dto.setQuantity(userStock.getQuantity());
        dto.setPurchasePrice(userStock.getPurchasePrice());
        dto.setPurchaseDate(userStock.getPurchaseDate());
        
        double currentPrice = userStock.getStock().getPrice();
        double currentValue = currentPrice * userStock.getQuantity();
        double purchaseValue = userStock.getPurchasePrice() * userStock.getQuantity();
        double profitLoss = currentValue - purchaseValue;
        
        dto.setCurrentValue(currentValue);
        dto.setProfitLoss(profitLoss);
        dto.setProfitLossPercent(purchaseValue != 0 ? (profitLoss / purchaseValue) * 100 : 0);
        
        return dto;
    }
}