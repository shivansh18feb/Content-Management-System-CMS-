package com.portfolio.cms.experience.dto;

import com.portfolio.cms.experience.entity.EmploymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExperienceDto {
    private Long id;
    private String company;
    private String position;
    private String description;
    private String location;
    private EmploymentType employmentType;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean current;
    private String technologies;
    private int displayOrder;
}
