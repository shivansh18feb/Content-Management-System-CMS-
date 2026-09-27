package com.portfolio.cms.dashboard.controller;

import com.portfolio.cms.audit.entity.AuditLog;
import com.portfolio.cms.audit.repository.AuditLogRepository;
import com.portfolio.cms.blog.entity.BlogStatus;
import com.portfolio.cms.blog.repository.BlogCategoryRepository;
import com.portfolio.cms.blog.repository.BlogRepository;
import com.portfolio.cms.blog.repository.BlogTagRepository;
import com.portfolio.cms.common.response.ApiResponse;
import com.portfolio.cms.contact.entity.MessageStatus;
import com.portfolio.cms.contact.repository.ContactMessageRepository;
import com.portfolio.cms.dashboard.dto.DashboardStatsDto;
import com.portfolio.cms.education.repository.EducationRepository;
import com.portfolio.cms.experience.repository.ExperienceRepository;
import com.portfolio.cms.media.repository.MediaRepository;
import com.portfolio.cms.project.entity.ProjectStatus;
import com.portfolio.cms.project.repository.ProjectRepository;
import com.portfolio.cms.service.repository.ServiceOfferingRepository;
import com.portfolio.cms.settings.repository.SiteSettingsRepository;
import com.portfolio.cms.skill.repository.SkillCategoryRepository;
import com.portfolio.cms.skill.repository.SkillRepository;
import com.portfolio.cms.social.repository.SocialLinkRepository;
import com.portfolio.cms.testimonial.repository.TestimonialRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/dashboard")
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Admin dashboard statistics")
public class DashboardController {

    private final ProjectRepository projectRepository;
    private final BlogRepository blogRepository;
    private final BlogCategoryRepository blogCategoryRepository;
    private final BlogTagRepository blogTagRepository;
    private final SkillRepository skillRepository;
    private final SkillCategoryRepository skillCategoryRepository;
    private final ExperienceRepository experienceRepository;
    private final EducationRepository educationRepository;
    private final ServiceOfferingRepository serviceOfferingRepository;
    private final TestimonialRepository testimonialRepository;
    private final ContactMessageRepository contactMessageRepository;
    private final MediaRepository mediaRepository;
    private final SocialLinkRepository socialLinkRepository;
    private final AuditLogRepository auditLogRepository;

    @GetMapping("/stats")
    @Operation(summary = "Get dashboard statistics", description = "Returns aggregate counts for all CMS entities and recent activity")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getStats() {

        // Recent 10 audit log entries
        List<AuditLog> recent = auditLogRepository.findAll(
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).getContent();

        List<DashboardStatsDto.RecentActivity> recentActivity = recent.stream()
                .map(log -> DashboardStatsDto.RecentActivity.builder()
                        .id(log.getId())
                        .action(log.getAction())
                        .entityType(log.getEntityType())
                        .entityId(log.getEntityId())
                        .userEmail(log.getUserEmail())
                        .createdAt(log.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        DashboardStatsDto stats = DashboardStatsDto.builder()
                // Projects
                .totalProjects(projectRepository.count())
                .publishedProjects(projectRepository.countByStatus(ProjectStatus.PUBLISHED))
                .draftProjects(projectRepository.countByStatus(ProjectStatus.DRAFT))
                .archivedProjects(projectRepository.countByStatus(ProjectStatus.ARCHIVED))
                // Blogs
                .totalBlogs(blogRepository.count())
                .publishedBlogs(blogRepository.countByStatus(BlogStatus.PUBLISHED))
                .draftBlogs(blogRepository.countByStatus(BlogStatus.DRAFT))
                // Skills
                .totalSkills(skillRepository.count())
                .totalSkillCategories(skillCategoryRepository.count())
                // Experience & Education
                .totalExperiences(experienceRepository.count())
                .totalEducations(educationRepository.count())
                // Services & Testimonials
                .totalServices(serviceOfferingRepository.count())
                .totalTestimonials(testimonialRepository.count())
                .publishedTestimonials(testimonialRepository.countByPublishedTrue())
                // Contact
                .totalMessages(contactMessageRepository.count())
                .unreadMessages(contactMessageRepository.countByStatus(MessageStatus.UNREAD))
                // Media
                .totalMediaFiles(mediaRepository.count())
                // Social
                .totalSocialLinks(socialLinkRepository.count())
                // Recent activity
                .recentActivity(recentActivity)
                .build();

        return ResponseEntity.ok(ApiResponse.ok(stats));
    }
}
