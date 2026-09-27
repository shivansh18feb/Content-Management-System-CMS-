package com.portfolio.cms.education.service;

import com.portfolio.cms.education.dto.EducationDto;
import com.portfolio.cms.education.dto.EducationRequest;

import java.util.List;

public interface EducationService {
    List<EducationDto> getAllEducation();
    EducationDto getEducationById(Long id);
    EducationDto createEducation(EducationRequest request, Long userId, String userEmail);
    EducationDto updateEducation(Long id, EducationRequest request, Long userId, String userEmail);
    void deleteEducation(Long id, Long userId, String userEmail);
}
