package com.portfolio.cms.testimonial.service;

import com.portfolio.cms.audit.service.AuditLogService;
import com.portfolio.cms.common.exception.ResourceNotFoundException;
import com.portfolio.cms.testimonial.dto.TestimonialDto;
import com.portfolio.cms.testimonial.dto.TestimonialRequest;
import com.portfolio.cms.testimonial.entity.Testimonial;
import com.portfolio.cms.testimonial.repository.TestimonialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestimonialServiceImpl implements TestimonialService {

    private final TestimonialRepository repository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    public List<TestimonialDto> getPublishedTestimonials() {
        return repository.findByPublishedTrueOrderByDisplayOrderAsc()
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestimonialDto> getFeaturedTestimonials() {
        return repository.findByPublishedTrueAndFeaturedTrueOrderByDisplayOrderAsc()
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TestimonialDto> getAllTestimonialsAdmin() {
        return repository.findAllByOrderByDisplayOrderAsc()
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TestimonialDto getTestimonialById(Long id) {
        Testimonial t = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Testimonial not found with id: " + id));
        return mapToDto(t);
    }

    @Override
    @Transactional
    public TestimonialDto createTestimonial(TestimonialRequest request, Long userId, String userEmail) {
        Testimonial t = Testimonial.builder()
                .name(request.getName().trim())
                .role(request.getRole().trim())
                .company(request.getCompany())
                .avatarUrl(request.getAvatarUrl())
                .content(request.getContent().trim())
                .rating(request.getRating())
                .featured(request.isFeatured())
                .published(request.isPublished())
                .displayOrder(request.getDisplayOrder())
                .build();

        Testimonial saved = repository.save(t);
        auditLogService.log(userId, userEmail, "CREATE", "Testimonial", String.valueOf(saved.getId()), null, "Created testimonial from: " + saved.getName());
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public TestimonialDto updateTestimonial(Long id, TestimonialRequest request, Long userId, String userEmail) {
        Testimonial t = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Testimonial not found with id: " + id));

        t.setName(request.getName().trim());
        t.setRole(request.getRole().trim());
        t.setCompany(request.getCompany());
        t.setAvatarUrl(request.getAvatarUrl());
        t.setContent(request.getContent().trim());
        t.setRating(request.getRating());
        t.setFeatured(request.isFeatured());
        t.setPublished(request.isPublished());
        t.setDisplayOrder(request.getDisplayOrder());

        Testimonial saved = repository.save(t);
        auditLogService.log(userId, userEmail, "UPDATE", "Testimonial", String.valueOf(saved.getId()), null, "Updated testimonial from: " + saved.getName());
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public void deleteTestimonial(Long id, Long userId, String userEmail) {
        Testimonial t = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Testimonial not found with id: " + id));
        String name = t.getName();
        repository.delete(t);
        auditLogService.log(userId, userEmail, "DELETE", "Testimonial", String.valueOf(id), null, "Deleted testimonial from: " + name);
    }

    @Override
    @Transactional
    public TestimonialDto togglePublished(Long id, Long userId, String userEmail) {
        Testimonial t = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Testimonial not found with id: " + id));
        t.setPublished(!t.isPublished());
        Testimonial saved = repository.save(t);
        String action = saved.isPublished() ? "PUBLISH" : "UNPUBLISH";
        auditLogService.log(userId, userEmail, action, "Testimonial", String.valueOf(saved.getId()), null, action + " testimonial from: " + saved.getName());
        return mapToDto(saved);
    }

    private TestimonialDto mapToDto(Testimonial t) {
        return TestimonialDto.builder()
                .id(t.getId())
                .name(t.getName())
                .role(t.getRole())
                .company(t.getCompany())
                .avatarUrl(t.getAvatarUrl())
                .content(t.getContent())
                .rating(t.getRating())
                .featured(t.isFeatured())
                .published(t.isPublished())
                .displayOrder(t.getDisplayOrder())
                .build();
    }
}
