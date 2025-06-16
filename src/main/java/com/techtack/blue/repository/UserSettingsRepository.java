package com.techtack.blue.repository;

import com.techtack.blue.model.UserSettings;
import com.techtack.blue.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    UserSettings findByUser(User user);
}