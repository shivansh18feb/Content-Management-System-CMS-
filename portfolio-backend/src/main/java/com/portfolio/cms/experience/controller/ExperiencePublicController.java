package com.portfolio.cms.experience.controller;

import com.portfolio.cms.common.response.ApiResponse;
import com.portfolio.cms.experience.dto.ExperienceDto;
import com.portfolio.cms.experience.service.ExperienceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/experience")
@RequiredArgsConstructor
@Tag(name = "Public Experience", description = "Public endpoints for career history and timeline")
public class ExperiencePublicController {

    private final ExperienceService experienceService;

    @GetMapping
    @Operation(summary = "Get Career Timeline", description = "Returns ordered career milestones and work experience")
    public ResponseEntity<ApiResponse<List<ExperienceDto>>> getExperience() {
        List<ExperienceDto> list = experienceService.getAllExperiences();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }
}
