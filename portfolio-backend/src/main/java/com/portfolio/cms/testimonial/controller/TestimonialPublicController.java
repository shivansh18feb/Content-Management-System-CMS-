package com.portfolio.cms.testimonial.controller;

import com.portfolio.cms.common.response.ApiResponse;
import com.portfolio.cms.testimonial.dto.TestimonialDto;
import com.portfolio.cms.testimonial.service.TestimonialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/testimonials")
@RequiredArgsConstructor
@Tag(name = "Public Testimonials", description = "Public endpoints for client reviews and endorsements")
public class TestimonialPublicController {

    private final TestimonialService testimonialService;

    @GetMapping
    @Operation(summary = "Get Published Testimonials", description = "Returns only published endorsements for public display")
    public ResponseEntity<ApiResponse<List<TestimonialDto>>> getTestimonials() {
        List<TestimonialDto> list = testimonialService.getPublishedTestimonials();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/featured")
    @Operation(summary = "Get Featured Testimonials", description = "Returns top endorsements for homepage carousel")
    public ResponseEntity<ApiResponse<List<TestimonialDto>>> getFeaturedTestimonials() {
        List<TestimonialDto> list = testimonialService.getFeaturedTestimonials();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }
}
