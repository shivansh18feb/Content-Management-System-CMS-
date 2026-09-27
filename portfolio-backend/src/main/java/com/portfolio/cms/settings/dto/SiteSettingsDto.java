package com.portfolio.cms.settings.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteSettingsDto {
    private Long id;

    @NotBlank(message = "Site name is required")
    private String siteName;

    private String siteDescription;
    private String logoUrl;
    private String faviconUrl;
    private String contactEmail;
    private String defaultSeoTitle;
    private String defaultSeoDescription;
    private String defaultSeoImage;
    private String resumeUrl;
}
