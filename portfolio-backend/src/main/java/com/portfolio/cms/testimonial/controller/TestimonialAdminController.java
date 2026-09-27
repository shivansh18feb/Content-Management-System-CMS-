package com.portfolio.cms.testimonial.controller;

import com.portfolio.cms.common.response.ApiResponse;
import com.portfolio.cms.security.UserPrincipal;
import com.portfolio.cms.testimonial.dto.TestimonialDto;
import com.portfolio.cms.testimonial.dto.TestimonialRequest;
import com.portfolio.cms.testimonial.service.TestimonialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/testimonials")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Admin Testimonials CMS", description = "CMS endpoints for moderating and publishing testimonials")
public class TestimonialAdminController {

    private final TestimonialService testimonialService;

    @GetMapping
    @Operation(summary = "List All Testimonials")
    public ResponseEntity<ApiResponse<List<TestimonialDto>>> getAllTestimonials() {
        return ResponseEntity.ok(ApiResponse.ok(testimonialService.getAllTestimonialsAdmin()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Testimonial by ID")
    public ResponseEntity<ApiResponse<TestimonialDto>> getTestimonialById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(testimonialService.getTestimonialById(id)));
    }

    @PostMapping
    @Operation(summary = "Create Testimonial")
    public ResponseEntity<ApiResponse<TestimonialDto>> createTestimonial(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody TestimonialRequest request) {
        TestimonialDto created = testimonialService.createTestimonial(request, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Testimonial created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Testimonial")
    public ResponseEntity<ApiResponse<TestimonialDto>> updateTestimonial(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody TestimonialRequest request) {
        TestimonialDto updated = testimonialService.updateTestimonial(id, request, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Testimonial updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Testimonial")
    public ResponseEntity<ApiResponse<Void>> deleteTestimonial(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        testimonialService.deleteTestimonial(id, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Testimonial deleted successfully"));
    }

    @PatchMapping("/{id}/toggle-publish")
    @Operation(summary = "Toggle Testimonial Publish Status")
    public ResponseEntity<ApiResponse<TestimonialDto>> togglePublish(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        TestimonialDto updated = testimonialService.togglePublished(id, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Testimonial publication status updated", updated));
    }
}
