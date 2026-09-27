package com.portfolio.cms.social.controller;

import com.portfolio.cms.common.response.ApiResponse;
import com.portfolio.cms.social.dto.SocialLinkDto;
import com.portfolio.cms.social.service.SocialLinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/social-links")
@RequiredArgsConstructor
@Tag(name = "Public Social Links", description = "Public endpoints for developer social profiles and contact channels")
public class SocialLinkPublicController {

    private final SocialLinkService socialLinkService;

    @GetMapping
    @Operation(summary = "Get Active Social Links", description = "Returns active social media profile URLs and icons")
    public ResponseEntity<ApiResponse<List<SocialLinkDto>>> getSocialLinks() {
        List<SocialLinkDto> list = socialLinkService.getActiveSocialLinks();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }
}
