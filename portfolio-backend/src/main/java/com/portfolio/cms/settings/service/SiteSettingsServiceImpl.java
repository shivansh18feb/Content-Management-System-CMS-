package com.portfolio.cms.settings.service;

import com.portfolio.cms.audit.service.AuditLogService;
import com.portfolio.cms.settings.dto.SiteSettingsDto;
import com.portfolio.cms.settings.entity.SiteSettings;
import com.portfolio.cms.settings.repository.SiteSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SiteSettingsServiceImpl implements SiteSettingsService {

    private final SiteSettingsRepository repository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    public SiteSettingsDto getSettings() {
        SiteSettings settings = repository.findFirstByOrderByIdAsc()
                .orElseGet(() -> repository.save(SiteSettings.builder()
                        .siteName("Developer Portfolio")
                        .siteDescription("Production portfolio built with Spring Boot and Next.js")
                        .build()));
        return mapToDto(settings);
    }

    @Override
    @Transactional
    public SiteSettingsDto updateSettings(SiteSettingsDto dto, Long userId, String userEmail) {
        SiteSettings settings = repository.findFirstByOrderByIdAsc()
                .orElseGet(SiteSettings::new);

        settings.setSiteName(dto.getSiteName().trim());
        settings.setSiteDescription(dto.getSiteDescription());
        settings.setLogoUrl(dto.getLogoUrl());
        settings.setFaviconUrl(dto.getFaviconUrl());
        settings.setContactEmail(dto.getContactEmail());
        settings.setDefaultSeoTitle(dto.getDefaultSeoTitle());
        settings.setDefaultSeoDescription(dto.getDefaultSeoDescription());
        settings.setDefaultSeoImage(dto.getDefaultSeoImage());
        settings.setResumeUrl(dto.getResumeUrl());

        SiteSettings saved = repository.save(settings);
        auditLogService.log(userId, userEmail, "UPDATE", "SiteSettings", String.valueOf(saved.getId()), null, "Updated global site settings");
        return mapToDto(saved);
    }

    private SiteSettingsDto mapToDto(SiteSettings s) {
        return SiteSettingsDto.builder()
                .id(s.getId())
                .siteName(s.getSiteName())
                .siteDescription(s.getSiteDescription())
                .logoUrl(s.getLogoUrl())
                .faviconUrl(s.getFaviconUrl())
                .contactEmail(s.getContactEmail())
                .defaultSeoTitle(s.getDefaultSeoTitle())
                .defaultSeoDescription(s.getDefaultSeoDescription())
                .defaultSeoImage(s.getDefaultSeoImage())
                .resumeUrl(s.getResumeUrl())
                .build();
    }
}
