package com.portfolio.cms.project.service;

import com.portfolio.cms.audit.service.AuditLogService;
import com.portfolio.cms.common.exception.DuplicateResourceException;
import com.portfolio.cms.common.exception.ResourceNotFoundException;
import com.portfolio.cms.common.response.PagedResponse;
import com.portfolio.cms.common.util.PageableUtils;
import com.portfolio.cms.common.util.SlugUtils;
import org.springframework.data.domain.Sort;
import com.portfolio.cms.project.dto.ProjectCreateRequest;
import com.portfolio.cms.project.dto.ProjectDetailDto;
import com.portfolio.cms.project.dto.ProjectSummaryDto;
import com.portfolio.cms.project.dto.ProjectUpdateRequest;
import com.portfolio.cms.project.entity.Project;
import com.portfolio.cms.project.entity.ProjectStatus;
import com.portfolio.cms.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProjectSummaryDto> getPublishedProjects(String search, String category, Pageable pageable) {
        Pageable snakePageable = PageableUtils.toSnakeCase(pageable, "display_order", Sort.Direction.ASC);
        Page<Project> page = projectRepository.findPublishedWithFilters(search, category, snakePageable);
        return PagedResponse.of(page.map(this::mapToSummaryDto));
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDetailDto getPublishedProjectBySlug(String slug) {
        Project project = projectRepository.findBySlugAndStatus(slug, ProjectStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with slug: " + slug));
        return mapToDetailDto(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectSummaryDto> getFeaturedProjects() {
        return projectRepository.findByStatusAndFeaturedTrueOrderByDisplayOrderAsc(ProjectStatus.PUBLISHED)
                .stream().map(this::mapToSummaryDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProjectSummaryDto> getAdminProjects(String search, String category, ProjectStatus status, Boolean featured, Pageable pageable) {
        String statusStr = status != null ? status.name() : null;
        Pageable snakePageable = PageableUtils.toSnakeCase(pageable, "display_order", Sort.Direction.ASC);
        Page<Project> page = projectRepository.findAdminWithFilters(search, category, statusStr, featured, snakePageable);
        return PagedResponse.of(page.map(this::mapToSummaryDto));
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDetailDto getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        return mapToDetailDto(project);
    }

    @Override
    @Transactional
    public ProjectDetailDto createProject(ProjectCreateRequest request, Long userId, String userEmail) {
        String baseSlug = StringUtils.hasText(request.getSlug()) ?
                SlugUtils.toSlug(request.getSlug()) : SlugUtils.toSlug(request.getTitle());

        String uniqueSlug = resolveUniqueSlug(baseSlug, null);

        Project project = Project.builder()
                .title(request.getTitle().trim())
                .slug(uniqueSlug)
                .shortDescription(request.getShortDescription().trim())
                .description(request.getDescription())
                .thumbnailUrl(request.getThumbnailUrl())
                .githubUrl(request.getGithubUrl())
                .liveUrl(request.getLiveUrl())
                .documentationUrl(request.getDocumentationUrl())
                .technologies(request.getTechnologies() != null ? new HashSet<>(request.getTechnologies()) : new HashSet<>())
                .category(StringUtils.hasText(request.getCategory()) ? request.getCategory() : "Full-Stack")
                .featured(request.isFeatured())
                .status(request.getStatus() != null ? request.getStatus() : ProjectStatus.DRAFT)
                .displayOrder(request.getDisplayOrder())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .seoTitle(request.getSeoTitle())
                .seoDescription(request.getSeoDescription())
                .build();

        Project saved = projectRepository.save(project);
        auditLogService.log(userId, userEmail, "CREATE", "Project", String.valueOf(saved.getId()), null, "Created project: " + saved.getTitle());
        return mapToDetailDto(saved);
    }

    @Override
    @Transactional
    public ProjectDetailDto updateProject(Long id, ProjectUpdateRequest request, Long userId, String userEmail) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        String baseSlug = StringUtils.hasText(request.getSlug()) ?
                SlugUtils.toSlug(request.getSlug()) : SlugUtils.toSlug(request.getTitle());

        if (projectRepository.existsBySlugAndIdNot(baseSlug, id)) {
            baseSlug = resolveUniqueSlug(baseSlug, id);
        }

        project.setTitle(request.getTitle().trim());
        project.setSlug(baseSlug);
        project.setShortDescription(request.getShortDescription().trim());
        project.setDescription(request.getDescription());
        project.setThumbnailUrl(request.getThumbnailUrl());
        project.setGithubUrl(request.getGithubUrl());
        project.setLiveUrl(request.getLiveUrl());
        project.setDocumentationUrl(request.getDocumentationUrl());
        if (request.getTechnologies() != null) {
            project.setTechnologies(new HashSet<>(request.getTechnologies()));
        }
        if (StringUtils.hasText(request.getCategory())) {
            project.setCategory(request.getCategory());
        }
        project.setFeatured(request.isFeatured());
        if (request.getStatus() != null) {
            project.setStatus(request.getStatus());
        }
        project.setDisplayOrder(request.getDisplayOrder());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setSeoTitle(request.getSeoTitle());
        project.setSeoDescription(request.getSeoDescription());

        Project saved = projectRepository.save(project);
        auditLogService.log(userId, userEmail, "UPDATE", "Project", String.valueOf(saved.getId()), null, "Updated project: " + saved.getTitle());
        return mapToDetailDto(saved);
    }

    @Override
    @Transactional
    public void deleteProject(Long id, Long userId, String userEmail) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        String title = project.getTitle();
        projectRepository.delete(project);
        auditLogService.log(userId, userEmail, "DELETE", "Project", String.valueOf(id), null, "Deleted project: " + title);
    }

    @Override
    @Transactional
    public ProjectDetailDto publishProject(Long id, Long userId, String userEmail) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        project.setStatus(ProjectStatus.PUBLISHED);
        Project saved = projectRepository.save(project);
        auditLogService.log(userId, userEmail, "PUBLISH", "Project", String.valueOf(saved.getId()), null, "Published project: " + saved.getTitle());
        return mapToDetailDto(saved);
    }

    @Override
    @Transactional
    public ProjectDetailDto unpublishProject(Long id, Long userId, String userEmail) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        project.setStatus(ProjectStatus.DRAFT);
        Project saved = projectRepository.save(project);
        auditLogService.log(userId, userEmail, "UNPUBLISH", "Project", String.valueOf(saved.getId()), null, "Unpublished project (reverted to DRAFT): " + saved.getTitle());
        return mapToDetailDto(saved);
    }

    @Override
    @Transactional
    public ProjectDetailDto archiveProject(Long id, Long userId, String userEmail) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
        project.setStatus(ProjectStatus.ARCHIVED);
        Project saved = projectRepository.save(project);
        auditLogService.log(userId, userEmail, "ARCHIVE", "Project", String.valueOf(saved.getId()), null, "Archived project: " + saved.getTitle());
        return mapToDetailDto(saved);
    }

    private String resolveUniqueSlug(String baseSlug, Long excludeId) {
        String slug = baseSlug;
        int count = 1;
        while ((excludeId == null && projectRepository.existsBySlug(slug)) ||
               (excludeId != null && projectRepository.existsBySlugAndIdNot(slug, excludeId))) {
            slug = baseSlug + "-" + count;
            count++;
        }
        return slug;
    }

    private ProjectSummaryDto mapToSummaryDto(Project project) {
        return ProjectSummaryDto.builder()
                .id(project.getId())
                .title(project.getTitle())
                .slug(project.getSlug())
                .shortDescription(project.getShortDescription())
                .thumbnailUrl(project.getThumbnailUrl())
                .githubUrl(project.getGithubUrl())
                .liveUrl(project.getLiveUrl())
                .documentationUrl(project.getDocumentationUrl())
                .technologies(project.getTechnologies())
                .category(project.getCategory())
                .featured(project.isFeatured())
                .status(project.getStatus())
                .displayOrder(project.getDisplayOrder())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .build();
    }

    private ProjectDetailDto mapToDetailDto(Project project) {
        return ProjectDetailDto.builder()
                .id(project.getId())
                .title(project.getTitle())
                .slug(project.getSlug())
                .shortDescription(project.getShortDescription())
                .description(project.getDescription())
                .thumbnailUrl(project.getThumbnailUrl())
                .githubUrl(project.getGithubUrl())
                .liveUrl(project.getLiveUrl())
                .documentationUrl(project.getDocumentationUrl())
                .technologies(project.getTechnologies())
                .category(project.getCategory())
                .featured(project.isFeatured())
                .status(project.getStatus())
                .displayOrder(project.getDisplayOrder())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .seoTitle(project.getSeoTitle())
                .seoDescription(project.getSeoDescription())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
}
