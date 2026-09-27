package com.portfolio.cms.project.controller;

import com.portfolio.cms.common.response.ApiResponse;
import com.portfolio.cms.common.response.PagedResponse;
import com.portfolio.cms.project.dto.ProjectCreateRequest;
import com.portfolio.cms.project.dto.ProjectDetailDto;
import com.portfolio.cms.project.dto.ProjectSummaryDto;
import com.portfolio.cms.project.dto.ProjectUpdateRequest;
import com.portfolio.cms.project.entity.ProjectStatus;
import com.portfolio.cms.project.service.ProjectService;
import com.portfolio.cms.security.UserPrincipal;
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

@RestController
@RequestMapping("/api/admin/projects")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Admin Projects CMS", description = "CMS endpoints for project lifecycle, drafting, publishing, and archiving")
public class ProjectAdminController {

    private final ProjectService projectService;

    @GetMapping
    @Operation(summary = "List Projects for Admin with Filters")
    public ResponseEntity<ApiResponse<PagedResponse<ProjectSummaryDto>>> getProjects(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) Boolean featured,
            @PageableDefault(size = 15, sort = "displayOrder", direction = Sort.Direction.ASC) Pageable pageable) {
        PagedResponse<ProjectSummaryDto> response = projectService.getAdminProjects(search, category, status, featured, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Project by ID")
    public ResponseEntity<ApiResponse<ProjectDetailDto>> getProjectById(@PathVariable Long id) {
        ProjectDetailDto project = projectService.getProjectById(id);
        return ResponseEntity.ok(ApiResponse.ok(project));
    }

    @PostMapping
    @Operation(summary = "Create Project (Draft or Published)")
    public ResponseEntity<ApiResponse<ProjectDetailDto>> createProject(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ProjectCreateRequest request) {
        ProjectDetailDto created = projectService.createProject(request, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Project created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Project")
    public ResponseEntity<ApiResponse<ProjectDetailDto>> updateProject(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ProjectUpdateRequest request) {
        ProjectDetailDto updated = projectService.updateProject(id, request, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Project updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Project")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        projectService.deleteProject(id, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Project deleted successfully"));
    }

    @RequestMapping(value = "/{id}/publish", method = {RequestMethod.POST, RequestMethod.PATCH})
    @Operation(summary = "Publish Project to Public Portfolio")
    public ResponseEntity<ApiResponse<ProjectDetailDto>> publishProject(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ProjectDetailDto published = projectService.publishProject(id, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Project published successfully", published));
    }

    @RequestMapping(value = "/{id}/unpublish", method = {RequestMethod.POST, RequestMethod.PATCH})
    @Operation(summary = "Unpublish Project (Revert to Draft)")
    public ResponseEntity<ApiResponse<ProjectDetailDto>> unpublishProject(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ProjectDetailDto unpublished = projectService.unpublishProject(id, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Project unpublished (reverted to draft)", unpublished));
    }

    @RequestMapping(value = "/{id}/archive", method = {RequestMethod.POST, RequestMethod.PATCH})
    @Operation(summary = "Archive Project")
    public ResponseEntity<ApiResponse<ProjectDetailDto>> archiveProject(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ProjectDetailDto archived = projectService.archiveProject(id, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Project archived", archived));
    }
}
