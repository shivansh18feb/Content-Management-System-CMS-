package com.portfolio.cms.dashboard.dto;

import com.portfolio.cms.audit.entity.AuditLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {

    // Projects
    private long totalProjects;
    private long publishedProjects;
    private long draftProjects;
    private long archivedProjects;

    // Blogs
    private long totalBlogs;
    private long publishedBlogs;
    private long draftBlogs;

    // Skills
    private long totalSkills;
    private long totalSkillCategories;

    // Experience & Education
    private long totalExperiences;
    private long totalEducations;

    // Services & Testimonials
    private long totalServices;
    private long totalTestimonials;
    private long publishedTestimonials;

    // Contact
    private long totalMessages;
    private long unreadMessages;

    // Media
    private long totalMediaFiles;

    // Social
    private long totalSocialLinks;

    // Audit
    private List<RecentActivity> recentActivity;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentActivity {
        private Long id;
        private String action;
        private String entityType;
        private String entityId;
        private String userEmail;
        private Instant createdAt;
    }
}
