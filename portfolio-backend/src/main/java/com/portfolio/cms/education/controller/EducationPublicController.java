package com.portfolio.cms.education.controller;

import com.portfolio.cms.common.response.ApiResponse;
import com.portfolio.cms.education.dto.EducationDto;
import com.portfolio.cms.education.service.EducationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/education")
@RequiredArgsConstructor
@Tag(name = "Public Education", description = "Public endpoints for academic background and degrees")
public class EducationPublicController {

    private final EducationService educationService;

    @GetMapping
    @Operation(summary = "Get Academic History", description = "Returns degrees and academic certifications")
    public ResponseEntity<ApiResponse<List<EducationDto>>> getEducation() {
        List<EducationDto> list = educationService.getAllEducation();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }
}
