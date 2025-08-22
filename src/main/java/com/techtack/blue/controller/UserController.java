package com.techtack.blue.controller;

import com.techtack.blue.dto.UserDto;
import com.techtack.blue.dto.mapper.UserDtoMapper;
import com.techtack.blue.exception.UserException;
import com.techtack.blue.model.User;
import com.techtack.blue.model.Stock;
import com.techtack.blue.service.UserService;
import com.techtack.blue.repository.UserRepository;
import com.techtack.blue.repository.StockRepository;

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
    private UserRepository userRepository;
    
    @Autowired
    private StockRepository stockRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@Valid @PathVariable Long userId) throws UserException {
        User user = userService.findUserById(userId);
        UserDto userDto = UserDtoMapper.toUserDto(user);
        return new ResponseEntity<UserDto>(userDto, HttpStatus.ACCEPTED);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable Long userId,
            @RequestBody UserDto userDto) throws UserException {

        User user = UserDtoMapper.toUser(userDto);
        User updatedUser = userService.updateUser(user, userId);
        UserDto updatedUserDto = UserDtoMapper.toUserDto(updatedUser);

        return new ResponseEntity<>(updatedUserDto, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@Valid @PathVariable Long userId) throws UserException {
        userService.deleteUser(userId);
        return new ResponseEntity<>("User Deleted", HttpStatus.OK);
    }

    @PostMapping("/{userId}/deposit")
    public ResponseEntity<UserDto> depositFunds(@PathVariable Long userId, @RequestParam double amount) throws UserException {
        User user = userService.findUserById(userId);
        user.setAccountBalance(user.getAccountBalance() + amount);
        user.setBuyingPower(user.getBuyingPower() + amount);

        User updatedUser = userRepository.save(user);
        UserDto userDto = UserDtoMapper.toUserDto(updatedUser);
        
        return new ResponseEntity<>(userDto, HttpStatus.OK);
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
