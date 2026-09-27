package com.portfolio.cms.settings.service;

import com.portfolio.cms.settings.dto.SiteSettingsDto;

public interface SiteSettingsService {
    SiteSettingsDto getSettings();
    SiteSettingsDto updateSettings(SiteSettingsDto dto, Long userId, String userEmail);
}
