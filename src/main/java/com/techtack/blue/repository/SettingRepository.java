package com.techtack.blue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techtack.blue.model.Setting;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {
    Setting findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
