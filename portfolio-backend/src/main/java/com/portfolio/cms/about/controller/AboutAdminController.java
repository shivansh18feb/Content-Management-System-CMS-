package com.portfolio.cms.about.controller;

import com.portfolio.cms.about.dto.AboutDto;
import com.portfolio.cms.about.service.AboutService;
import com.portfolio.cms.common.response.ApiResponse;
import com.portfolio.cms.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/about")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Admin About CMS", description = "CMS endpoints for modifying profile bio, headline, availability, and resume")
public class AboutAdminController {

    private final AboutService aboutService;

    @GetMapping
    @Operation(summary = "Get Profile for CMS Editor")
    public ResponseEntity<ApiResponse<AboutDto>> getAbout() {
        AboutDto about = aboutService.getAbout();
        return ResponseEntity.ok(ApiResponse.ok(about));
    }

    @PutMapping
    @Operation(summary = "Update Profile Content")
    public ResponseEntity<ApiResponse<AboutDto>> updateAbout(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                             @Valid @RequestBody AboutDto dto) {
        AboutDto updated = aboutService.updateAbout(dto, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", updated));
    }
}
