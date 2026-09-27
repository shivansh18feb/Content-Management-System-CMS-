package com.portfolio.cms.social.controller;

import com.portfolio.cms.common.response.ApiResponse;
import com.portfolio.cms.security.UserPrincipal;
import com.portfolio.cms.social.dto.SocialLinkDto;
import com.portfolio.cms.social.dto.SocialLinkRequest;
import com.portfolio.cms.social.service.SocialLinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/social-links")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Admin Social Links CMS", description = "CMS endpoints for configuring social channel links")
public class SocialLinkAdminController {

    private final SocialLinkService socialLinkService;

    @GetMapping
    @Operation(summary = "List All Social Links (Admin)")
    public ResponseEntity<ApiResponse<List<SocialLinkDto>>> getAllSocialLinks() {
        return ResponseEntity.ok(ApiResponse.ok(socialLinkService.getAllSocialLinksAdmin()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Social Link by ID")
    public ResponseEntity<ApiResponse<SocialLinkDto>> getSocialLinkById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(socialLinkService.getSocialLinkById(id)));
    }

    @PostMapping
    @Operation(summary = "Create Social Link")
    public ResponseEntity<ApiResponse<SocialLinkDto>> createSocialLink(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody SocialLinkRequest request) {
        SocialLinkDto created = socialLinkService.createSocialLink(request, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Social link added successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Social Link")
    public ResponseEntity<ApiResponse<SocialLinkDto>> updateSocialLink(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody SocialLinkRequest request) {
        SocialLinkDto updated = socialLinkService.updateSocialLink(id, request, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Social link updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Social Link")
    public ResponseEntity<ApiResponse<Void>> deleteSocialLink(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        socialLinkService.deleteSocialLink(id, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Social link deleted successfully"));
    }
}
