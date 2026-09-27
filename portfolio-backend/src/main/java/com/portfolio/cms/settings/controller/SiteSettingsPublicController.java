package com.portfolio.cms.settings.controller;

import com.portfolio.cms.common.response.ApiResponse;
import com.portfolio.cms.settings.dto.SiteSettingsDto;
import com.portfolio.cms.settings.service.SiteSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/settings")
@RequiredArgsConstructor
@Tag(name = "Public Settings", description = "Public endpoints for global site metadata, branding, and resume link")
public class SiteSettingsPublicController {

    private final SiteSettingsService siteSettingsService;

    @GetMapping
    @Operation(summary = "Get Public Site Settings", description = "Returns global site title, descriptions, logo, and active resume URL")
    public ResponseEntity<ApiResponse<SiteSettingsDto>> getPublicSettings() {
        SiteSettingsDto settings = siteSettingsService.getSettings();
        return ResponseEntity.ok(ApiResponse.ok(settings));
    }
}
