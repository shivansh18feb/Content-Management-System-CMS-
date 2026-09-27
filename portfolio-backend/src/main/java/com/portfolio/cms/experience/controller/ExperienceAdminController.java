package com.portfolio.cms.experience.controller;

import com.portfolio.cms.common.response.ApiResponse;
import com.portfolio.cms.experience.dto.ExperienceDto;
import com.portfolio.cms.experience.dto.ExperienceRequest;
import com.portfolio.cms.experience.service.ExperienceService;
import com.portfolio.cms.security.UserPrincipal;
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
@RequestMapping("/api/admin/experience")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Admin Experience CMS", description = "CMS endpoints for modifying career timeline entries")
public class ExperienceAdminController {

    private final ExperienceService experienceService;

    @GetMapping
    @Operation(summary = "List All Experience Entries")
    public ResponseEntity<ApiResponse<List<ExperienceDto>>> getExperiences() {
        return ResponseEntity.ok(ApiResponse.ok(experienceService.getAllExperiences()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Experience Entry by ID")
    public ResponseEntity<ApiResponse<ExperienceDto>> getExperienceById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(experienceService.getExperienceById(id)));
    }

    @PostMapping
    @Operation(summary = "Create Experience Entry")
    public ResponseEntity<ApiResponse<ExperienceDto>> createExperience(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ExperienceRequest request) {
        ExperienceDto created = experienceService.createExperience(request, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Experience entry created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Experience Entry")
    public ResponseEntity<ApiResponse<ExperienceDto>> updateExperience(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ExperienceRequest request) {
        ExperienceDto updated = experienceService.updateExperience(id, request, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Experience entry updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Experience Entry")
    public ResponseEntity<ApiResponse<Void>> deleteExperience(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        experienceService.deleteExperience(id, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Experience entry deleted successfully"));
    }
}
