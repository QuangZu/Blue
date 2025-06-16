package com.techtack.blue.dto.mapper;

import com.techtack.blue.dto.UserSettingsDto;
import com.techtack.blue.model.UserSettings;

public class UserSettingsDtoMapper {

    public static UserSettingsDto toUserSettingsDto(UserSettings settings) {
        UserSettingsDto dto = new UserSettingsDto();
        dto.setId(settings.getId());
        dto.setUserId(settings.getUser().getId());
        dto.setLightModeEnabled(settings.isLightModeEnabled());
        return dto;
    }
}