package com.portfolio.cms.experience.dto;

import com.portfolio.cms.experience.entity.EmploymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceRequest {

    @NotBlank(message = "Company name is required")
    private String company;

    @NotBlank(message = "Position is required")
    private String position;

    @NotBlank(message = "Description is required")
    private String description;

    private String location;

    @Builder.Default
    private EmploymentType employmentType = EmploymentType.FULL_TIME;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    private LocalDate endDate;

    @Builder.Default
    private boolean current = false;

    private String technologies;

    @Builder.Default
    private int displayOrder = 0;
}
