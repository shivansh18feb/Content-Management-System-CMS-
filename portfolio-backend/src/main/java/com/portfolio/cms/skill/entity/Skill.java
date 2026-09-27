package com.portfolio.cms.skill.entity;

import com.portfolio.cms.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private SkillCategory category;

    @Column(nullable = false)
    private String name;

    @Column
    private String icon;

    @Column(nullable = false)
    @Builder.Default
    private int proficiency = 80;

    @Column(name = "years_of_experience")
    @Builder.Default
    private BigDecimal yearsOfExperience = BigDecimal.valueOf(1.0);

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private int displayOrder = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean featured = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}
