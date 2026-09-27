package com.portfolio.cms.social.service;

import com.portfolio.cms.social.dto.SocialLinkDto;
import com.portfolio.cms.social.dto.SocialLinkRequest;

import java.util.List;

public interface SocialLinkService {
    List<SocialLinkDto> getActiveSocialLinks();
    List<SocialLinkDto> getAllSocialLinksAdmin();
    SocialLinkDto getSocialLinkById(Long id);
    SocialLinkDto createSocialLink(SocialLinkRequest request, Long userId, String userEmail);
    SocialLinkDto updateSocialLink(Long id, SocialLinkRequest request, Long userId, String userEmail);
    void deleteSocialLink(Long id, Long userId, String userEmail);
}
