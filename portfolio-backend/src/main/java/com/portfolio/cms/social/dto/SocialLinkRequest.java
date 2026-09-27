package com.portfolio.cms.social.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialLinkRequest {

    @NotBlank(message = "Platform name is required")
    private String platform;

    @NotBlank(message = "URL is required")
    private String url;

    @NotBlank(message = "Icon name is required")
    private String icon;

    @Builder.Default
    private int displayOrder = 0;

    @Builder.Default
    private boolean active = true;
}
