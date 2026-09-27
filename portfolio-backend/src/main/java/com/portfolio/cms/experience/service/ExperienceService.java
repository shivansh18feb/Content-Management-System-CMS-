package com.portfolio.cms.experience.service;

import com.portfolio.cms.experience.dto.ExperienceDto;
import com.portfolio.cms.experience.dto.ExperienceRequest;

import java.util.List;

public interface ExperienceService {
    List<ExperienceDto> getAllExperiences();
    ExperienceDto getExperienceById(Long id);
    ExperienceDto createExperience(ExperienceRequest request, Long userId, String userEmail);
    ExperienceDto updateExperience(Long id, ExperienceRequest request, Long userId, String userEmail);
    void deleteExperience(Long id, Long userId, String userEmail);
}
