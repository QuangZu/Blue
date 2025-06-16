package com.techtack.blue.controller;

import com.techtack.blue.dto.UserSettingsDto;
import com.techtack.blue.dto.mapper.UserDtoMapper;
import com.techtack.blue.model.User;
import com.techtack.blue.model.UserSettings;
import com.techtack.blue.response.ApiResponse;
import com.techtack.blue.service.UserService;
import com.techtack.blue.service.UserSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserSettingsService userSettingsService;
    
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile(@RequestHeader("Authorization") String jwt) {
        User user = userService.findUserProfileByJwt(jwt);
        UserSettings settings = userSettingsService.getUserSettings(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("user", UserDtoMapper.toUserDto(user));
        response.put("accountBalance", user.getAccountBalance());
        
        UserSettingsDto settingsDto = new UserSettingsDto();
        settingsDto.setId(settings.getId());
        settingsDto.setUserId(user.getId());
        settingsDto.setLightModeEnabled(settings.isLightModeEnabled());
        response.put("settings", settingsDto);
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @PutMapping("/username")
    public ResponseEntity<ApiResponse> updateUsername(
            @RequestHeader("Authorization") String jwt,
            @RequestBody Map<String, String> request) {
        
        User user = userService.findUserProfileByJwt(jwt);
        String newUsername = request.get("username");
        
        userService.updateUsername(user.getId(), newUsername);
        
        ApiResponse response = new ApiResponse();
        response.setMessage("Username updated successfully");
        response.setStatus(true);
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @PutMapping("/password")
    public ResponseEntity<ApiResponse> updatePassword(
            @RequestHeader("Authorization") String jwt,
            @RequestBody Map<String, String> request) {
        
        User user = userService.findUserProfileByJwt(jwt);
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        
        userService.updatePassword(user.getId(), oldPassword, newPassword);
        
        ApiResponse response = new ApiResponse();
        response.setMessage("Password updated successfully");
        response.setStatus(true);
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @PutMapping("/theme")
    public ResponseEntity<ApiResponse> updateTheme(
            @RequestHeader("Authorization") String jwt,
            @RequestBody Map<String, Boolean> request) {
        
        User user = userService.findUserProfileByJwt(jwt);
        boolean lightModeEnabled = request.get("lightModeEnabled");
        
        userSettingsService.updateLightMode(user, lightModeEnabled);
        
        ApiResponse response = new ApiResponse();
        response.setMessage("Theme preference updated successfully");
        response.setStatus(true);
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}