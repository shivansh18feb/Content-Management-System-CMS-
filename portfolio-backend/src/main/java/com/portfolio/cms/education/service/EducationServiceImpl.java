package com.portfolio.cms.education.service;

import com.portfolio.cms.audit.service.AuditLogService;
import com.portfolio.cms.common.exception.ResourceNotFoundException;
import com.portfolio.cms.education.dto.EducationDto;
import com.portfolio.cms.education.dto.EducationRequest;
import com.portfolio.cms.education.entity.Education;
import com.portfolio.cms.education.repository.EducationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationServiceImpl implements EducationService {

    private final EducationRepository educationRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    public List<EducationDto> getAllEducation() {
        return educationRepository.findAllByOrderByDisplayOrderAsc()
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EducationDto getEducationById(Long id) {
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Education record not found with id: " + id));
        return mapToDto(education);
    }

    @Override
    @Transactional
    public EducationDto createEducation(EducationRequest request, Long userId, String userEmail) {
        Education education = Education.builder()
                .institution(request.getInstitution().trim())
                .degree(request.getDegree().trim())
                .fieldOfStudy(request.getFieldOfStudy().trim())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .grade(request.getGrade())
                .location(request.getLocation())
                .displayOrder(request.getDisplayOrder())
                .build();

        Education saved = educationRepository.save(education);
        auditLogService.log(userId, userEmail, "CREATE", "Education", String.valueOf(saved.getId()), null, "Added education: " + saved.getDegree());
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public EducationDto updateEducation(Long id, EducationRequest request, Long userId, String userEmail) {
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Education record not found with id: " + id));

        education.setInstitution(request.getInstitution().trim());
        education.setDegree(request.getDegree().trim());
        education.setFieldOfStudy(request.getFieldOfStudy().trim());
        education.setDescription(request.getDescription());
        education.setStartDate(request.getStartDate());
        education.setEndDate(request.getEndDate());
        education.setGrade(request.getGrade());
        education.setLocation(request.getLocation());
        education.setDisplayOrder(request.getDisplayOrder());

        Education saved = educationRepository.save(education);
        auditLogService.log(userId, userEmail, "UPDATE", "Education", String.valueOf(saved.getId()), null, "Updated education: " + saved.getDegree());
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public void deleteEducation(Long id, Long userId, String userEmail) {
        Education education = educationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Education record not found with id: " + id));
        String degree = education.getDegree();
        educationRepository.delete(education);
        auditLogService.log(userId, userEmail, "DELETE", "Education", String.valueOf(id), null, "Deleted education: " + degree);
    }

    private EducationDto mapToDto(Education edu) {
        return EducationDto.builder()
                .id(edu.getId())
                .institution(edu.getInstitution())
                .degree(edu.getDegree())
                .fieldOfStudy(edu.getFieldOfStudy())
                .description(edu.getDescription())
                .startDate(edu.getStartDate())
                .endDate(edu.getEndDate())
                .grade(edu.getGrade())
                .location(edu.getLocation())
                .displayOrder(edu.getDisplayOrder())
                .build();
    }
}
