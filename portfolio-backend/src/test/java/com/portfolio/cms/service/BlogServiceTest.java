package com.portfolio.cms.service;

import com.portfolio.cms.audit.service.AuditLogService;
import com.portfolio.cms.blog.dto.BlogCreateRequest;
import com.portfolio.cms.blog.dto.BlogDetailDto;
import com.portfolio.cms.blog.entity.Blog;
import com.portfolio.cms.blog.entity.BlogCategory;
import com.portfolio.cms.blog.entity.BlogStatus;
import com.portfolio.cms.blog.repository.BlogCategoryRepository;
import com.portfolio.cms.blog.repository.BlogRepository;
import com.portfolio.cms.blog.repository.BlogTagRepository;
import com.portfolio.cms.blog.service.BlogServiceImpl;
import com.portfolio.cms.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlogServiceTest {

    @Mock
    private BlogRepository blogRepository;

    @Mock
    private BlogCategoryRepository categoryRepository;

    @Mock
    private BlogTagRepository tagRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private BlogServiceImpl blogService;

    private Blog sampleBlog;
    private BlogCategory sampleCategory;

    @BeforeEach
    void setUp() {
        sampleCategory = BlogCategory.builder()
                .name("Architecture")
                .slug("architecture")
                .build();
        sampleCategory.setId(1L);

        sampleBlog = Blog.builder()
                .title("Modern Microservices Patterns")
                .slug("modern-microservices-patterns")
                .excerpt("A guide to modern resilience and event driven design.")
                .content("In distributed architectures, resilience is not accidental...")
                .category(sampleCategory)
                .tags(new HashSet<>())
                .status(BlogStatus.DRAFT)
                .readingTimeMinutes(5)
                .author("admin@portfolio.com")
                .build();
        sampleBlog.setId(100L);
        sampleBlog.setCreatedAt(Instant.now());
    }

    @Test
    @DisplayName("createBlog creates draft article with slug and logs audit")
    void createBlog_Success() {
        BlogCreateRequest request = BlogCreateRequest.builder()
                .title("Modern Microservices Patterns")
                .excerpt("A guide to modern resilience and event driven design.")
                .content("In distributed architectures, resilience is not accidental...")
                .categoryId(1L)
                .tagNames(Collections.emptyList())
                .status(BlogStatus.DRAFT)
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(sampleCategory));
        when(blogRepository.existsBySlug("modern-microservices-patterns")).thenReturn(false);
        when(blogRepository.save(any(Blog.class))).thenReturn(sampleBlog);

        BlogDetailDto created = blogService.createBlog(request, 1L, "admin@portfolio.com");

        assertThat(created).isNotNull();
        assertThat(created.getId()).isEqualTo(100L);
        assertThat(created.getTitle()).isEqualTo("Modern Microservices Patterns");
        assertThat(created.getStatus()).isEqualTo(BlogStatus.DRAFT);

        verify(blogRepository, times(1)).save(any(Blog.class));
        verify(auditLogService, times(1)).log(
                eq(1L),
                eq("admin@portfolio.com"),
                eq("CREATE"),
                eq("Blog"),
                eq("100"),
                any(),
                anyString()
        );
    }

    @Test
    @DisplayName("publishBlog updates status to PUBLISHED and sets publishedAt")
    void publishBlog_Success() {
        when(blogRepository.findById(100L)).thenReturn(Optional.of(sampleBlog));
        when(blogRepository.save(any(Blog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BlogDetailDto published = blogService.publishBlog(100L, 1L, "admin@portfolio.com");

        assertThat(published.getStatus()).isEqualTo(BlogStatus.PUBLISHED);
        assertThat(published.getPublishedAt()).isNotNull();

        verify(auditLogService, times(1)).log(
                eq(1L),
                eq("admin@portfolio.com"),
                eq("PUBLISH"),
                eq("Blog"),
                eq("100"),
                any(),
                anyString()
        );
    }

    @Test
    @DisplayName("unpublishBlog reverts status to DRAFT")
    void unpublishBlog_Success() {
        sampleBlog.setStatus(BlogStatus.PUBLISHED);
        sampleBlog.setPublishedAt(Instant.now());

        when(blogRepository.findById(100L)).thenReturn(Optional.of(sampleBlog));
        when(blogRepository.save(any(Blog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BlogDetailDto unpublished = blogService.unpublishBlog(100L, 1L, "admin@portfolio.com");

        assertThat(unpublished.getStatus()).isEqualTo(BlogStatus.DRAFT);
    }

    @Test
    @DisplayName("getBlogById throws ResourceNotFoundException when id is missing")
    void getBlogById_NotFound() {
        when(blogRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> blogService.getBlogById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Blog post not found with id: 999");
    }
}
