package com.portfolio.cms.project.controller;

import com.portfolio.cms.common.response.ApiResponse;
import com.portfolio.cms.common.response.PagedResponse;
import com.portfolio.cms.project.dto.ProjectDetailDto;
import com.portfolio.cms.project.dto.ProjectSummaryDto;
import com.portfolio.cms.project.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/projects")
@RequiredArgsConstructor
@Tag(name = "Public Projects", description = "Public endpoints for exploring published portfolio projects")
public class ProjectPublicController {

    private final ProjectService projectService;

    @GetMapping
    @Operation(summary = "List Published Projects", description = "Returns only published projects with search and category filtering")
    public ResponseEntity<ApiResponse<PagedResponse<ProjectSummaryDto>>> getProjects(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @PageableDefault(size = 9) Pageable pageable) {
        PagedResponse<ProjectSummaryDto> response = projectService.getPublishedProjects(search, category, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/featured")
    @Operation(summary = "Get Featured Projects", description = "Returns published projects flagged as featured for homepage showcase")
    public ResponseEntity<ApiResponse<List<ProjectSummaryDto>>> getFeaturedProjects() {
        List<ProjectSummaryDto> featured = projectService.getFeaturedProjects();
        return ResponseEntity.ok(ApiResponse.ok(featured));
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get Project Case Study by Slug", description = "Returns deep case study details and SEO information for published project")
    public ResponseEntity<ApiResponse<ProjectDetailDto>> getProjectBySlug(@PathVariable String slug) {
        ProjectDetailDto project = projectService.getPublishedProjectBySlug(slug);
        return ResponseEntity.ok(ApiResponse.ok(project));
    }
}
