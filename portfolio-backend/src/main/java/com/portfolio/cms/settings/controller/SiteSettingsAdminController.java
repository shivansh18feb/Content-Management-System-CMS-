package com.portfolio.cms.settings.controller;

import com.portfolio.cms.common.response.ApiResponse;
import com.portfolio.cms.security.UserPrincipal;
import com.portfolio.cms.settings.dto.SiteSettingsDto;
import com.portfolio.cms.settings.service.SiteSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/settings")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Admin Settings CMS", description = "CMS endpoints for modifying global website configurations")
public class SiteSettingsAdminController {

    private final SiteSettingsService siteSettingsService;

    @GetMapping
    @Operation(summary = "Get Settings (Admin)")
    public ResponseEntity<ApiResponse<SiteSettingsDto>> getSettings() {
        return ResponseEntity.ok(ApiResponse.ok(siteSettingsService.getSettings()));
    }

    @PutMapping
    @Operation(summary = "Update Settings")
    public ResponseEntity<ApiResponse<SiteSettingsDto>> updateSettings(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody SiteSettingsDto dto) {
        SiteSettingsDto updated = siteSettingsService.updateSettings(dto, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Settings updated successfully", updated));
    }
}
