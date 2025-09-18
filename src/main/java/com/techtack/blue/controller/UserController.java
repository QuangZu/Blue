package com.techtack.blue.controller;

import com.techtack.blue.dto.*;
import com.techtack.blue.dto.mapper.UserDtoMapper;
import com.techtack.blue.exception.UserException;
import com.techtack.blue.model.User;
import com.techtack.blue.model.Stock;
import com.techtack.blue.service.UserService;
import com.techtack.blue.service.DepositService;
import com.techtack.blue.repository.UserRepository;
import com.techtack.blue.repository.StockRepository;
import com.techtack.blue.service.NotificationService;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StockRepository stockRepository;
    
    @Autowired
    private DepositService depositService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@Valid @PathVariable Long userId) throws UserException {
        User user = userService.findUserById(userId);
        UserDto userDto = UserDtoMapper.toUserDto(user);
        return new ResponseEntity<UserDto>(userDto, HttpStatus.ACCEPTED);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(user, userId);
        
        try {
            String deviceToken = "user_device_token_" + userId;
            notificationService.sendNotificationToDevice(
                deviceToken,
                "Profile Updated",
                "Your profile information has been successfully updated.",
                Map.of(
                    "type", "profile_update",
                    "user_id", userId.toString()
                )
            );
        } catch (Exception e) {
            System.err.println("Failed to send profile update notification: " + e.getMessage());
        }
        
            return ResponseEntity.ok(updatedUser);
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@Valid @PathVariable Long userId) throws UserException {
        userService.deleteUser(userId);
        return new ResponseEntity<>("User Deleted", HttpStatus.OK);
    }
    
    @GetMapping("/{userId}/balance")
    public ResponseEntity<Map<String, Object>> checkBalanceSufficiency(@PathVariable Long userId, @RequestParam String symbol, @RequestParam int quantity) throws UserException {
        User user = userService.findUserById(userId);
        Stock stock = stockRepository.findBySymbol(symbol);

        double totalCost = stock.getPrice() * quantity;
        boolean sufficientFunds = user.getAccountBalance() >= totalCost;
        
        Map<String, Object> response = new HashMap<>();
        response.put("accountBalance", user.getAccountBalance());
        response.put("buyingPower", user.getBuyingPower());
        response.put("totalCost", totalCost);
        response.put("sufficientFunds", sufficientFunds);
        response.put("shortfall", sufficientFunds ? 0 : totalCost - user.getAccountBalance());

        return ResponseEntity.ok(response);
    }
}
