package com.portfolio.cms.skill.service;

import com.portfolio.cms.common.response.PagedResponse;
import com.portfolio.cms.skill.dto.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SkillService {
    List<SkillCategoryDto> getGroupedSkills();
    List<SkillDto> getFeaturedSkills();
    List<SkillCategoryDto> getAllCategories();
    SkillCategoryDto createCategory(CreateCategoryRequest request);
    void deleteCategory(Long id);

    PagedResponse<SkillDto> getSkillsAdmin(String search, Long categoryId, Boolean active, Pageable pageable);
    SkillDto getSkillById(Long id);
    SkillDto createSkill(CreateSkillRequest request, Long userId, String userEmail);
    SkillDto updateSkill(Long id, UpdateSkillRequest request, Long userId, String userEmail);
    void deleteSkill(Long id, Long userId, String userEmail);
}
