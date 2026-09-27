package com.portfolio.cms.skill.controller;

import com.portfolio.cms.common.response.ApiResponse;
import com.portfolio.cms.skill.dto.SkillCategoryDto;
import com.portfolio.cms.skill.dto.SkillDto;
import com.portfolio.cms.skill.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/skills")
@RequiredArgsConstructor
@Tag(name = "Public Skills", description = "Public endpoints for technical competencies and skill matrices")
public class SkillPublicController {

    private final SkillService skillService;

    @GetMapping
    @Operation(summary = "Get Skills Grouped by Category", description = "Returns active skills categorized for the public skill matrix")
    public ResponseEntity<ApiResponse<List<SkillCategoryDto>>> getGroupedSkills() {
        List<SkillCategoryDto> skills = skillService.getGroupedSkills();
        return ResponseEntity.ok(ApiResponse.ok(skills));
    }

    @GetMapping("/featured")
    @Operation(summary = "Get Featured Core Skills", description = "Returns core skills highlighted on the portfolio homepage")
    public ResponseEntity<ApiResponse<List<SkillDto>>> getFeaturedSkills() {
        List<SkillDto> featured = skillService.getFeaturedSkills();
        return ResponseEntity.ok(ApiResponse.ok(featured));
    }

    @GetMapping("/categories")
    @Operation(summary = "Get Skill Categories", description = "Returns category listing for filtering")
    public ResponseEntity<ApiResponse<List<SkillCategoryDto>>> getCategories() {
        List<SkillCategoryDto> categories = skillService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.ok(categories));
    }
}
