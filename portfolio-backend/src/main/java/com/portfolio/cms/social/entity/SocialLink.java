package com.portfolio.cms.social.entity;

import com.portfolio.cms.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "social_links")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialLink extends BaseEntity {

    @Column(nullable = false)
    private String platform;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String icon;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private int displayOrder = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}
