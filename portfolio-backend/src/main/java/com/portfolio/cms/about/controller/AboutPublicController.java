package com.portfolio.cms.about.controller;

import com.portfolio.cms.about.dto.AboutDto;
import com.portfolio.cms.about.service.AboutService;
import com.portfolio.cms.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/about")
@RequiredArgsConstructor
@Tag(name = "Public About", description = "Public endpoints for retrieving developer profile and biographical data")
public class AboutPublicController {

    private final AboutService aboutService;

    @GetMapping
    @Operation(summary = "Get Public About Profile", description = "Returns public profile headline, bios, contact and social links")
    public ResponseEntity<ApiResponse<AboutDto>> getPublicAbout() {
        AboutDto about = aboutService.getAbout();
        return ResponseEntity.ok(ApiResponse.ok(about));
    }
}
