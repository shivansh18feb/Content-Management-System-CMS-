package com.portfolio.cms.service;

import com.portfolio.cms.audit.service.AuditLogService;
import com.portfolio.cms.common.exception.ResourceNotFoundException;
import com.portfolio.cms.project.dto.ProjectCreateRequest;
import com.portfolio.cms.project.dto.ProjectDetailDto;
import com.portfolio.cms.project.entity.Project;
import com.portfolio.cms.project.entity.ProjectStatus;
import com.portfolio.cms.project.repository.ProjectRepository;
import com.portfolio.cms.project.service.ProjectServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Test
    @DisplayName("Should create a project with generated unique slug and audit log")
    void shouldCreateProjectSuccessfully() {
        ProjectCreateRequest request = ProjectCreateRequest.builder()
                .title("New Enterprise App")
                .shortDescription("A cutting edge distributed system")
                .description("Detailed case study content")
                .category("Full-Stack")
                .technologies(Set.of("Java", "Spring Boot", "React"))
                .status(ProjectStatus.DRAFT)
                .build();

        when(projectRepository.existsBySlug("new-enterprise-app")).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> {
            Project p = inv.getArgument(0);
            p.setId(10L);
            return p;
        });

        ProjectDetailDto dto = projectService.createProject(request, 1L, "admin@portfolio.com");

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getSlug()).isEqualTo("new-enterprise-app");
        assertThat(dto.getTitle()).isEqualTo("New Enterprise App");
        verify(auditLogService).log(eq(1L), eq("admin@portfolio.com"), eq("CREATE"), eq("Project"), eq("10"), any(), any());
    }

    @Test
    @DisplayName("Should resolve slug collision by appending count suffix")
    void shouldResolveSlugCollision() {
        ProjectCreateRequest request = ProjectCreateRequest.builder()
                .title("New Enterprise App")
                .shortDescription("Another project with same name")
                .description("Case study")
                .build();

        // "new-enterprise-app" exists, but "new-enterprise-app-1" does not
        when(projectRepository.existsBySlug("new-enterprise-app")).thenReturn(true);
        when(projectRepository.existsBySlug("new-enterprise-app-1")).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> {
            Project p = inv.getArgument(0);
            p.setId(11L);
            return p;
        });

        ProjectDetailDto dto = projectService.createProject(request, 1L, "admin@portfolio.com");

        assertThat(dto.getSlug()).isEqualTo("new-enterprise-app-1");
    }

    @Test
    @DisplayName("Should publish project and update status to PUBLISHED")
    void shouldPublishProject() {
        Project existing = Project.builder()
                .title("Draft Project")
                .slug("draft-project")
                .status(ProjectStatus.DRAFT)
                .build();
        existing.setId(5L);

        when(projectRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> inv.getArgument(0));

        ProjectDetailDto result = projectService.publishProject(5L, 1L, "admin@portfolio.com");

        assertThat(result.getStatus()).isEqualTo(ProjectStatus.PUBLISHED);
        verify(auditLogService).log(eq(1L), eq("admin@portfolio.com"), eq("PUBLISH"), eq("Project"), eq("5"), any(), any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when project does not exist")
    void shouldThrowWhenProjectNotFound() {
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getProjectById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Project not found with id: 999");
    }
}
