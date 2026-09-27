package com.portfolio.cms.project.dto;

import com.portfolio.cms.project.entity.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
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
public class ProjectUpdateRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String slug;

    @NotBlank(message = "Short description is required")
    private String shortDescription;

    @NotBlank(message = "Detailed description is required")
    private String description;

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
    private String seoTitle;
    private String seoDescription;
}
