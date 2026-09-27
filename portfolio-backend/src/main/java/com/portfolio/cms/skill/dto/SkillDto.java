package com.portfolio.cms.skill.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillDto {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String icon;
    private int proficiency;
    private BigDecimal yearsOfExperience;
    private int displayOrder;
    private boolean featured;
    private boolean active;
}
