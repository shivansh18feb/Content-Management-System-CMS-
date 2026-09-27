package com.portfolio.cms.skill.service;

import com.portfolio.cms.audit.service.AuditLogService;
import com.portfolio.cms.common.exception.DuplicateResourceException;
import com.portfolio.cms.common.exception.ResourceNotFoundException;
import com.portfolio.cms.common.response.PagedResponse;
import com.portfolio.cms.skill.dto.*;
import com.portfolio.cms.skill.entity.Skill;
import com.portfolio.cms.skill.entity.SkillCategory;
import com.portfolio.cms.skill.repository.SkillCategoryRepository;
import com.portfolio.cms.skill.repository.SkillRepository;
import com.portfolio.cms.common.util.PageableUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final SkillCategoryRepository categoryRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    public List<SkillCategoryDto> getGroupedSkills() {
        List<SkillCategory> categories = categoryRepository.findAllByOrderByDisplayOrderAsc();
        return categories.stream().map(cat -> {
            List<SkillDto> skills = cat.getSkills().stream()
                    .filter(Skill::isActive)
                    .map(this::mapToDto)
                    .collect(Collectors.toList());

            return SkillCategoryDto.builder()
                    .id(cat.getId())
                    .name(cat.getName())
                    .displayOrder(cat.getDisplayOrder())
                    .skills(skills)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillDto> getFeaturedSkills() {
        return skillRepository.findByActiveTrueAndFeaturedTrueOrderByDisplayOrderAsc()
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillCategoryDto> getAllCategories() {
        return categoryRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(cat -> SkillCategoryDto.builder()
                        .id(cat.getId())
                        .name(cat.getName())
                        .displayOrder(cat.getDisplayOrder())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SkillCategoryDto createCategory(CreateCategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Category already exists: " + request.getName());
        }
        SkillCategory category = SkillCategory.builder()
                .name(request.getName().trim())
                .displayOrder(request.getDisplayOrder())
                .build();
        SkillCategory saved = categoryRepository.save(category);
        return SkillCategoryDto.builder()
                .id(saved.getId())
                .name(saved.getName())
                .displayOrder(saved.getDisplayOrder())
                .build();
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        SkillCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill category not found with id: " + id));
        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<SkillDto> getSkillsAdmin(String search, Long categoryId, Boolean active, Pageable pageable) {
        Pageable snakePageable = PageableUtils.toSnakeCase(pageable, "display_order", Sort.Direction.ASC);
        Page<Skill> skills = skillRepository.findWithFilters(search, categoryId, active, snakePageable);
        return PagedResponse.of(skills.map(this::mapToDto));
    }

    @Override
    @Transactional(readOnly = true)
    public SkillDto getSkillById(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));
        return mapToDto(skill);
    }

    @Override
    @Transactional
    public SkillDto createSkill(CreateSkillRequest request, Long userId, String userEmail) {
        SkillCategory category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        }

        Skill skill = Skill.builder()
                .category(category)
                .name(request.getName().trim())
                .icon(request.getIcon())
                .proficiency(request.getProficiency())
                .yearsOfExperience(request.getYearsOfExperience())
                .displayOrder(request.getDisplayOrder())
                .featured(request.isFeatured())
                .active(request.isActive())
                .build();

        Skill saved = skillRepository.save(skill);
        auditLogService.log(userId, userEmail, "CREATE", "Skill", String.valueOf(saved.getId()), null, "Created skill: " + saved.getName());
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public SkillDto updateSkill(Long id, UpdateSkillRequest request, Long userId, String userEmail) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));

        if (request.getCategoryId() != null) {
            SkillCategory category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
            skill.setCategory(category);
        } else {
            skill.setCategory(null);
        }

        skill.setName(request.getName().trim());
        skill.setIcon(request.getIcon());
        skill.setProficiency(request.getProficiency());
        skill.setYearsOfExperience(request.getYearsOfExperience());
        skill.setDisplayOrder(request.getDisplayOrder());
        skill.setFeatured(request.isFeatured());
        skill.setActive(request.isActive());

        Skill saved = skillRepository.save(skill);
        auditLogService.log(userId, userEmail, "UPDATE", "Skill", String.valueOf(saved.getId()), null, "Updated skill: " + saved.getName());
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public void deleteSkill(Long id, Long userId, String userEmail) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));
        String skillName = skill.getName();
        skillRepository.delete(skill);
        auditLogService.log(userId, userEmail, "DELETE", "Skill", String.valueOf(id), null, "Deleted skill: " + skillName);
    }

    private SkillDto mapToDto(Skill skill) {
        return SkillDto.builder()
                .id(skill.getId())
                .categoryId(skill.getCategory() != null ? skill.getCategory().getId() : null)
                .categoryName(skill.getCategory() != null ? skill.getCategory().getName() : null)
                .name(skill.getName())
                .icon(skill.getIcon())
                .proficiency(skill.getProficiency())
                .yearsOfExperience(skill.getYearsOfExperience())
                .displayOrder(skill.getDisplayOrder())
                .featured(skill.isFeatured())
                .active(skill.isActive())
                .build();
    }
}
