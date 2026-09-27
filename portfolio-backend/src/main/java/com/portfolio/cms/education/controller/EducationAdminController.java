package com.portfolio.cms.education.controller;

import com.portfolio.cms.common.response.ApiResponse;
import com.portfolio.cms.education.dto.EducationDto;
import com.portfolio.cms.education.dto.EducationRequest;
import com.portfolio.cms.education.service.EducationService;
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
@RequestMapping("/api/admin/education")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Admin Education CMS", description = "CMS endpoints for modifying education entries")
public class EducationAdminController {

    private final EducationService educationService;

    @GetMapping
    @Operation(summary = "List All Education Entries")
    public ResponseEntity<ApiResponse<List<EducationDto>>> getEducation() {
        return ResponseEntity.ok(ApiResponse.ok(educationService.getAllEducation()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Education Entry by ID")
    public ResponseEntity<ApiResponse<EducationDto>> getEducationById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(educationService.getEducationById(id)));
    }

    @PostMapping
    @Operation(summary = "Create Education Entry")
    public ResponseEntity<ApiResponse<EducationDto>> createEducation(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody EducationRequest request) {
        EducationDto created = educationService.createEducation(request, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Education entry created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Education Entry")
    public ResponseEntity<ApiResponse<EducationDto>> updateEducation(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody EducationRequest request) {
        EducationDto updated = educationService.updateEducation(id, request, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Education entry updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Education Entry")
    public ResponseEntity<ApiResponse<Void>> deleteEducation(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        educationService.deleteEducation(id, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Education entry deleted successfully"));
    }
}
