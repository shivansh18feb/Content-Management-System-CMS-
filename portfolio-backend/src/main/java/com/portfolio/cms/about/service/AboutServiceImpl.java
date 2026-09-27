package com.portfolio.cms.about.service;

import com.portfolio.cms.about.dto.AboutDto;
import com.portfolio.cms.about.entity.About;
import com.portfolio.cms.about.repository.AboutRepository;
import com.portfolio.cms.audit.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AboutServiceImpl implements AboutService {

    private final AboutRepository aboutRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    public AboutDto getAbout() {
        About about = aboutRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> aboutRepository.save(About.builder()
                        .headline("Senior Full-Stack Engineer & Architect")
                        .shortBio("Passionate developer crafting modern distributed systems and web apps.")
                        .longBio("Experienced with Java 21, Spring Boot, PostgreSQL, and React/Next.js.")
                        .availability("AVAILABLE")
                        .yearsOfExperience(5)
                        .build()));

        return mapToDto(about);
    }

    @Override
    @Transactional
    public AboutDto updateAbout(AboutDto dto, Long userId, String userEmail) {
        About about = aboutRepository.findFirstByOrderByIdAsc()
                .orElseGet(About::new);

        about.setHeadline(dto.getHeadline());
        about.setShortBio(dto.getShortBio());
        about.setLongBio(dto.getLongBio());
        about.setProfileImageUrl(dto.getProfileImageUrl());
        about.setLocation(dto.getLocation());
        about.setEmail(dto.getEmail());
        about.setPhone(dto.getPhone());
        about.setResumeUrl(dto.getResumeUrl());
        about.setAvailability(dto.getAvailability() != null ? dto.getAvailability() : "AVAILABLE");
        about.setYearsOfExperience(dto.getYearsOfExperience());
        about.setGithubUrl(dto.getGithubUrl());
        about.setLinkedinUrl(dto.getLinkedinUrl());
        about.setTwitterUrl(dto.getTwitterUrl());

        About saved = aboutRepository.save(about);

        auditLogService.log(userId, userEmail, "UPDATE", "About", String.valueOf(saved.getId()), null, "Updated about profile");

        return mapToDto(saved);
    }

    private AboutDto mapToDto(About about) {
        return AboutDto.builder()
                .id(about.getId())
                .headline(about.getHeadline())
                .shortBio(about.getShortBio())
                .longBio(about.getLongBio())
                .profileImageUrl(about.getProfileImageUrl())
                .location(about.getLocation())
                .email(about.getEmail())
                .phone(about.getPhone())
                .resumeUrl(about.getResumeUrl())
                .availability(about.getAvailability())
                .yearsOfExperience(about.getYearsOfExperience())
                .githubUrl(about.getGithubUrl())
                .linkedinUrl(about.getLinkedinUrl())
                .twitterUrl(about.getTwitterUrl())
                .build();
    }
}
