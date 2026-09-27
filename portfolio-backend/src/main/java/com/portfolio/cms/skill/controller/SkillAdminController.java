package com.portfolio.cms.skill.controller;

import com.portfolio.cms.common.response.ApiResponse;
import com.portfolio.cms.common.response.PagedResponse;
import com.portfolio.cms.security.UserPrincipal;
import com.portfolio.cms.skill.dto.*;
import com.portfolio.cms.skill.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/skills")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Admin Skills CMS", description = "CMS endpoints for skill management, categories, and proficiency ratings")
public class SkillAdminController {

    private final SkillService skillService;

    @GetMapping
    @Operation(summary = "List Skills with Pagination & Search")
    public ResponseEntity<ApiResponse<PagedResponse<SkillDto>>> getSkills(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 20, sort = "displayOrder", direction = Sort.Direction.ASC) Pageable pageable) {
        PagedResponse<SkillDto> response = skillService.getSkillsAdmin(search, categoryId, active, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Skill by ID")
    public ResponseEntity<ApiResponse<SkillDto>> getSkillById(@PathVariable Long id) {
        SkillDto skill = skillService.getSkillById(id);
        return ResponseEntity.ok(ApiResponse.ok(skill));
    }

    @PostMapping
    @Operation(summary = "Create New Skill")
    public ResponseEntity<ApiResponse<SkillDto>> createSkill(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody CreateSkillRequest request) {
        SkillDto created = skillService.createSkill(request, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Skill created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Existing Skill")
    public ResponseEntity<ApiResponse<SkillDto>> updateSkill(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UpdateSkillRequest request) {
        SkillDto updated = skillService.updateSkill(id, request, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Skill updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Skill")
    public ResponseEntity<ApiResponse<Void>> deleteSkill(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        skillService.deleteSkill(id, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Skill deleted successfully"));
    }

    @GetMapping("/categories")
    @Operation(summary = "List All Skill Categories")
    public ResponseEntity<ApiResponse<List<SkillCategoryDto>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.ok(skillService.getAllCategories()));
    }

    @PostMapping("/categories")
    @Operation(summary = "Create Skill Category")
    public ResponseEntity<ApiResponse<SkillCategoryDto>> createCategory(
            @Valid @RequestBody CreateCategoryRequest request) {
        SkillCategoryDto created = skillService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Category created successfully", created));
    }

    @DeleteMapping("/categories/{id}")
    @Operation(summary = "Delete Skill Category")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        skillService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.ok("Category deleted successfully"));
    }
}
