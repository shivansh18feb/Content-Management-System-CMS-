package com.portfolio.cms.experience.service;

import com.portfolio.cms.audit.service.AuditLogService;
import com.portfolio.cms.common.exception.ResourceNotFoundException;
import com.portfolio.cms.experience.dto.ExperienceDto;
import com.portfolio.cms.experience.dto.ExperienceRequest;
import com.portfolio.cms.experience.entity.Experience;
import com.portfolio.cms.experience.repository.ExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExperienceServiceImpl implements ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    public List<ExperienceDto> getAllExperiences() {
        return experienceRepository.findAllByOrderByDisplayOrderAsc()
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ExperienceDto getExperienceById(Long id) {
        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experience not found with id: " + id));
        return mapToDto(experience);
    }

    @Override
    @Transactional
    public ExperienceDto createExperience(ExperienceRequest request, Long userId, String userEmail) {
        Experience experience = Experience.builder()
                .company(request.getCompany().trim())
                .position(request.getPosition().trim())
                .description(request.getDescription().trim())
                .location(request.getLocation())
                .employmentType(request.getEmploymentType())
                .startDate(request.getStartDate())
                .endDate(request.isCurrent() ? null : request.getEndDate())
                .current(request.isCurrent())
                .technologies(request.getTechnologies())
                .displayOrder(request.getDisplayOrder())
                .build();

        Experience saved = experienceRepository.save(experience);
        auditLogService.log(userId, userEmail, "CREATE", "Experience", String.valueOf(saved.getId()), null, "Created experience at: " + saved.getCompany());
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public ExperienceDto updateExperience(Long id, ExperienceRequest request, Long userId, String userEmail) {
        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experience not found with id: " + id));

        experience.setCompany(request.getCompany().trim());
        experience.setPosition(request.getPosition().trim());
        experience.setDescription(request.getDescription().trim());
        experience.setLocation(request.getLocation());
        experience.setEmploymentType(request.getEmploymentType());
        experience.setStartDate(request.getStartDate());
        experience.setEndDate(request.isCurrent() ? null : request.getEndDate());
        experience.setCurrent(request.isCurrent());
        experience.setTechnologies(request.getTechnologies());
        experience.setDisplayOrder(request.getDisplayOrder());

        Experience saved = experienceRepository.save(experience);
        auditLogService.log(userId, userEmail, "UPDATE", "Experience", String.valueOf(saved.getId()), null, "Updated experience at: " + saved.getCompany());
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public void deleteExperience(Long id, Long userId, String userEmail) {
        Experience experience = experienceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experience not found with id: " + id));
        String company = experience.getCompany();
        experienceRepository.delete(experience);
        auditLogService.log(userId, userEmail, "DELETE", "Experience", String.valueOf(id), null, "Deleted experience at: " + company);
    }

    private ExperienceDto mapToDto(Experience exp) {
        return ExperienceDto.builder()
                .id(exp.getId())
                .company(exp.getCompany())
                .position(exp.getPosition())
                .description(exp.getDescription())
                .location(exp.getLocation())
                .employmentType(exp.getEmploymentType())
                .startDate(exp.getStartDate())
                .endDate(exp.getEndDate())
                .current(exp.isCurrent())
                .technologies(exp.getTechnologies())
                .displayOrder(exp.getDisplayOrder())
                .build();
    }
}
