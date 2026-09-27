package com.portfolio.cms.skill.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSkillRequest {

    private Long categoryId;

    @NotBlank(message = "Skill name is required")
    private String name;

    private String icon;

    @Min(value = 0, message = "Proficiency must be at least 0")
    @Max(value = 100, message = "Proficiency cannot exceed 100")
    private int proficiency;

    private BigDecimal yearsOfExperience;
    private int displayOrder;
    private boolean featured;
    private boolean active;
}
