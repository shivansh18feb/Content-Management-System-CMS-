package com.portfolio.cms.project.service;

import com.portfolio.cms.common.response.PagedResponse;
import com.portfolio.cms.project.dto.ProjectCreateRequest;
import com.portfolio.cms.project.dto.ProjectDetailDto;
import com.portfolio.cms.project.dto.ProjectSummaryDto;
import com.portfolio.cms.project.dto.ProjectUpdateRequest;
import com.portfolio.cms.project.entity.ProjectStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjectService {
    PagedResponse<ProjectSummaryDto> getPublishedProjects(String search, String category, Pageable pageable);
    ProjectDetailDto getPublishedProjectBySlug(String slug);
    List<ProjectSummaryDto> getFeaturedProjects();

    PagedResponse<ProjectSummaryDto> getAdminProjects(String search, String category, ProjectStatus status, Boolean featured, Pageable pageable);
    ProjectDetailDto getProjectById(Long id);
    ProjectDetailDto createProject(ProjectCreateRequest request, Long userId, String userEmail);
    ProjectDetailDto updateProject(Long id, ProjectUpdateRequest request, Long userId, String userEmail);
    void deleteProject(Long id, Long userId, String userEmail);

    ProjectDetailDto publishProject(Long id, Long userId, String userEmail);
    ProjectDetailDto unpublishProject(Long id, Long userId, String userEmail);
    ProjectDetailDto archiveProject(Long id, Long userId, String userEmail);
}
