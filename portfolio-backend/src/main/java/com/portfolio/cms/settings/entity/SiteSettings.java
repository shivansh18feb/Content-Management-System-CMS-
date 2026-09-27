package com.portfolio.cms.settings.entity;

import com.portfolio.cms.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "site_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteSettings extends BaseEntity {

    @Column(name = "site_name", nullable = false)
    @Builder.Default
    private String siteName = "Developer Portfolio";

    @Column(name = "site_description", columnDefinition = "TEXT")
    private String siteDescription;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "favicon_url")
    private String faviconUrl;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "default_seo_title")
    private String defaultSeoTitle;

    @Column(name = "default_seo_description", columnDefinition = "TEXT")
    private String defaultSeoDescription;

    @Column(name = "default_seo_image")
    private String defaultSeoImage;

    @Column(name = "resume_url")
    private String resumeUrl;
}
