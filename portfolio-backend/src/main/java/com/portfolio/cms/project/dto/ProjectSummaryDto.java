package com.portfolio.cms.project.dto;

import com.portfolio.cms.project.entity.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSummaryDto {
    private Long id;
    private String title;
    private String slug;
    private String shortDescription;
    private String thumbnailUrl;
    private String githubUrl;
    private String liveUrl;
    private String documentationUrl;
    private Set<String> technologies;
    private String category;
    private boolean featured;
    private ProjectStatus status;
    private int displayOrder;
    private LocalDate startDate;
    private LocalDate endDate;
}
