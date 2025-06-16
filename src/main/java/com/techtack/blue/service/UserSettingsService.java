package com.techtack.blue.service;

import com.techtack.blue.model.User;
import com.techtack.blue.model.UserSettings;
import com.techtack.blue.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserSettingsService {

    @Autowired
    private UserSettingsRepository userSettingsRepository;
    
    public UserSettings getUserSettings(User user) {
        UserSettings settings = userSettingsRepository.findByUser(user);
        if (settings == null) {
            settings = new UserSettings(user);
            userSettingsRepository.save(settings);
        }
        return settings;
    }
    
    public UserSettings updateLightMode(User user, boolean lightModeEnabled) {
        UserSettings settings = getUserSettings(user);
        settings.setLightModeEnabled(lightModeEnabled);
        return userSettingsRepository.save(settings);
    }
}