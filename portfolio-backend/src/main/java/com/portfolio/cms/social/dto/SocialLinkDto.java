package com.portfolio.cms.social.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialLinkDto {
    private Long id;
    private String platform;
    private String url;
    private String icon;
    private int displayOrder;
    private boolean active;
}
