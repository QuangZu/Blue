package com.techtack.blue.dto;

import lombok.Data;

@Data
public class UserSettingsDto {
    private Long id;
    private Long userId;
    private boolean lightModeEnabled;
}