package com.portfolio.cms.social.service;

import com.portfolio.cms.audit.service.AuditLogService;
import com.portfolio.cms.common.exception.ResourceNotFoundException;
import com.portfolio.cms.social.dto.SocialLinkDto;
import com.portfolio.cms.social.dto.SocialLinkRequest;
import com.portfolio.cms.social.entity.SocialLink;
import com.portfolio.cms.social.repository.SocialLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SocialLinkServiceImpl implements SocialLinkService {

    private final SocialLinkRepository repository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    public List<SocialLinkDto> getActiveSocialLinks() {
        return repository.findByActiveTrueOrderByDisplayOrderAsc()
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SocialLinkDto> getAllSocialLinksAdmin() {
        return repository.findAllByOrderByDisplayOrderAsc()
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SocialLinkDto getSocialLinkById(Long id) {
        SocialLink link = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Social link not found with id: " + id));
        return mapToDto(link);
    }

    @Override
    @Transactional
    public SocialLinkDto createSocialLink(SocialLinkRequest request, Long userId, String userEmail) {
        SocialLink link = SocialLink.builder()
                .platform(request.getPlatform().trim())
                .url(request.getUrl().trim())
                .icon(request.getIcon().trim())
                .displayOrder(request.getDisplayOrder())
                .active(request.isActive())
                .build();

        SocialLink saved = repository.save(link);
        auditLogService.log(userId, userEmail, "CREATE", "SocialLink", String.valueOf(saved.getId()), null, "Added social link: " + saved.getPlatform());
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public SocialLinkDto updateSocialLink(Long id, SocialLinkRequest request, Long userId, String userEmail) {
        SocialLink link = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Social link not found with id: " + id));

        link.setPlatform(request.getPlatform().trim());
        link.setUrl(request.getUrl().trim());
        link.setIcon(request.getIcon().trim());
        link.setDisplayOrder(request.getDisplayOrder());
        link.setActive(request.isActive());

        SocialLink saved = repository.save(link);
        auditLogService.log(userId, userEmail, "UPDATE", "SocialLink", String.valueOf(saved.getId()), null, "Updated social link: " + saved.getPlatform());
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public void deleteSocialLink(Long id, Long userId, String userEmail) {
        SocialLink link = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Social link not found with id: " + id));
        String platform = link.getPlatform();
        repository.delete(link);
        auditLogService.log(userId, userEmail, "DELETE", "SocialLink", String.valueOf(id), null, "Deleted social link: " + platform);
    }

    private SocialLinkDto mapToDto(SocialLink link) {
        return SocialLinkDto.builder()
                .id(link.getId())
                .platform(link.getPlatform())
                .url(link.getUrl())
                .icon(link.getIcon())
                .displayOrder(link.getDisplayOrder())
                .active(link.isActive())
                .build();
    }
}
