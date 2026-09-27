package com.portfolio.cms.testimonial.service;

import com.portfolio.cms.testimonial.dto.TestimonialDto;
import com.portfolio.cms.testimonial.dto.TestimonialRequest;

import java.util.List;

public interface TestimonialService {
    List<TestimonialDto> getPublishedTestimonials();
    List<TestimonialDto> getFeaturedTestimonials();
    List<TestimonialDto> getAllTestimonialsAdmin();
    TestimonialDto getTestimonialById(Long id);
    TestimonialDto createTestimonial(TestimonialRequest request, Long userId, String userEmail);
    TestimonialDto updateTestimonial(Long id, TestimonialRequest request, Long userId, String userEmail);
    void deleteTestimonial(Long id, Long userId, String userEmail);
    TestimonialDto togglePublished(Long id, Long userId, String userEmail);
}
