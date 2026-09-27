package com.portfolio.cms.blog.service;

import com.portfolio.cms.audit.service.AuditLogService;
import com.portfolio.cms.blog.dto.*;
import com.portfolio.cms.blog.entity.Blog;
import com.portfolio.cms.blog.entity.BlogCategory;
import com.portfolio.cms.blog.entity.BlogStatus;
import com.portfolio.cms.blog.entity.BlogTag;
import com.portfolio.cms.blog.repository.BlogCategoryRepository;
import com.portfolio.cms.blog.repository.BlogRepository;
import com.portfolio.cms.blog.repository.BlogTagRepository;
import com.portfolio.cms.common.exception.DuplicateResourceException;
import com.portfolio.cms.common.exception.ResourceNotFoundException;
import com.portfolio.cms.common.response.PagedResponse;
import com.portfolio.cms.common.util.PageableUtils;
import com.portfolio.cms.common.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final BlogCategoryRepository categoryRepository;
    private final BlogTagRepository tagRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<BlogSummaryDto> getPublishedBlogs(String search, String categorySlug, String tagSlug, Pageable pageable) {
        Pageable snakePageable = PageableUtils.toSnakeCase(pageable, "published_at", Sort.Direction.DESC);
        Page<Blog> page = blogRepository.findPublishedWithFilters(search, categorySlug, tagSlug, snakePageable);
        return PagedResponse.of(page.map(this::mapToSummaryDto));
    }

    @Override
    @Transactional(readOnly = true)
    public BlogDetailDto getPublishedBlogBySlug(String slug) {
        Blog blog = blogRepository.findBySlugAndStatus(slug, BlogStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found with slug: " + slug));
        return mapToDetailDto(blog);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogCategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogTagDto> getAllTags() {
        return tagRepository.findAll().stream()
                .map(this::mapToTagDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<BlogSummaryDto> getAdminBlogs(String search, Long categoryId, BlogStatus status, Pageable pageable) {
        String statusStr = status != null ? status.name() : null;
        Pageable snakePageable = PageableUtils.toSnakeCase(pageable, "created_at", Sort.Direction.DESC);
        Page<Blog> page = blogRepository.findAdminWithFilters(search, categoryId, statusStr, snakePageable);
        return PagedResponse.of(page.map(this::mapToSummaryDto));
    }

    @Override
    @Transactional(readOnly = true)
    public BlogDetailDto getBlogById(Long id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found with id: " + id));
        return mapToDetailDto(blog);
    }

    @Override
    @Transactional
    public BlogDetailDto createBlog(BlogCreateRequest request, Long userId, String userEmail) {
        String baseSlug = StringUtils.hasText(request.getSlug()) ?
                SlugUtils.toSlug(request.getSlug()) : SlugUtils.toSlug(request.getTitle());

        String uniqueSlug = resolveUniqueSlug(baseSlug, null);

        BlogCategory category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        }

        Set<BlogTag> tags = resolveTags(request.getTagNames());
        int readingTime = calculateReadingTime(request.getContent());

        Instant publishedAt = (request.getStatus() == BlogStatus.PUBLISHED) ? Instant.now() : null;

        Blog blog = Blog.builder()
                .title(request.getTitle().trim())
                .slug(uniqueSlug)
                .excerpt(request.getExcerpt().trim())
                .content(request.getContent())
                .coverImageUrl(request.getCoverImageUrl())
                .author(StringUtils.hasText(request.getAuthor()) ? request.getAuthor().trim() : userEmail)
                .category(category)
                .tags(tags)
                .status(request.getStatus() != null ? request.getStatus() : BlogStatus.DRAFT)
                .publishedAt(publishedAt)
                .readingTimeMinutes(readingTime)
                .seoTitle(request.getSeoTitle())
                .seoDescription(request.getSeoDescription())
                .seoKeywords(request.getSeoKeywords())
                .build();

        Blog saved = blogRepository.save(blog);
        auditLogService.log(userId, userEmail, "CREATE", "Blog", String.valueOf(saved.getId()), null, "Created blog post: " + saved.getTitle());
        return mapToDetailDto(saved);
    }

    @Override
    @Transactional
    public BlogDetailDto updateBlog(Long id, BlogUpdateRequest request, Long userId, String userEmail) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found with id: " + id));

        String baseSlug = StringUtils.hasText(request.getSlug()) ?
                SlugUtils.toSlug(request.getSlug()) : SlugUtils.toSlug(request.getTitle());

        if (blogRepository.existsBySlugAndIdNot(baseSlug, id)) {
            baseSlug = resolveUniqueSlug(baseSlug, id);
        }

        if (request.getCategoryId() != null) {
            BlogCategory category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
            blog.setCategory(category);
        } else {
            blog.setCategory(null);
        }

        if (request.getTagNames() != null) {
            blog.setTags(resolveTags(request.getTagNames()));
        }

        if (blog.getStatus() != BlogStatus.PUBLISHED && request.getStatus() == BlogStatus.PUBLISHED && blog.getPublishedAt() == null) {
            blog.setPublishedAt(Instant.now());
        }

        blog.setTitle(request.getTitle().trim());
        blog.setSlug(baseSlug);
        blog.setExcerpt(request.getExcerpt().trim());
        blog.setContent(request.getContent());
        blog.setCoverImageUrl(request.getCoverImageUrl());
        if (StringUtils.hasText(request.getAuthor())) {
            blog.setAuthor(request.getAuthor().trim());
        }
        if (request.getStatus() != null) {
            blog.setStatus(request.getStatus());
        }
        blog.setReadingTimeMinutes(calculateReadingTime(request.getContent()));
        blog.setSeoTitle(request.getSeoTitle());
        blog.setSeoDescription(request.getSeoDescription());
        blog.setSeoKeywords(request.getSeoKeywords());

        Blog saved = blogRepository.save(blog);
        auditLogService.log(userId, userEmail, "UPDATE", "Blog", String.valueOf(saved.getId()), null, "Updated blog post: " + saved.getTitle());
        return mapToDetailDto(saved);
    }

    @Override
    @Transactional
    public void deleteBlog(Long id, Long userId, String userEmail) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found with id: " + id));
        String title = blog.getTitle();
        blogRepository.delete(blog);
        auditLogService.log(userId, userEmail, "DELETE", "Blog", String.valueOf(id), null, "Deleted blog post: " + title);
    }

    @Override
    @Transactional
    public BlogDetailDto publishBlog(Long id, Long userId, String userEmail) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found with id: " + id));

        blog.setStatus(BlogStatus.PUBLISHED);
        if (blog.getPublishedAt() == null) {
            blog.setPublishedAt(Instant.now());
        }
        Blog saved = blogRepository.save(blog);
        auditLogService.log(userId, userEmail, "PUBLISH", "Blog", String.valueOf(saved.getId()), null, "Published blog post: " + saved.getTitle());
        return mapToDetailDto(saved);
    }

    @Override
    @Transactional
    public BlogDetailDto unpublishBlog(Long id, Long userId, String userEmail) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found with id: " + id));

        blog.setStatus(BlogStatus.DRAFT);
        Blog saved = blogRepository.save(blog);
        auditLogService.log(userId, userEmail, "UNPUBLISH", "Blog", String.valueOf(saved.getId()), null, "Unpublished blog post (reverted to DRAFT): " + saved.getTitle());
        return mapToDetailDto(saved);
    }

    @Override
    @Transactional
    public BlogDetailDto archiveBlog(Long id, Long userId, String userEmail) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found with id: " + id));

        blog.setStatus(BlogStatus.ARCHIVED);
        Blog saved = blogRepository.save(blog);
        auditLogService.log(userId, userEmail, "ARCHIVE", "Blog", String.valueOf(saved.getId()), null, "Archived blog post: " + saved.getTitle());
        return mapToDetailDto(saved);
    }

    @Override
    @Transactional
    public BlogCategoryDto createCategory(CreateBlogCategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Category already exists: " + request.getName());
        }
        String slug = SlugUtils.toSlug(request.getName());
        BlogCategory category = BlogCategory.builder()
                .name(request.getName().trim())
                .slug(slug)
                .description(request.getDescription())
                .build();
        return mapToCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        BlogCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog category not found with id: " + id));
        categoryRepository.delete(category);
    }

    @Override
    @Transactional
    public BlogTagDto createTag(CreateBlogTagRequest request) {
        if (tagRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Tag already exists: " + request.getName());
        }
        String slug = SlugUtils.toSlug(request.getName());
        BlogTag tag = BlogTag.builder()
                .name(request.getName().trim())
                .slug(slug)
                .build();
        return mapToTagDto(tagRepository.save(tag));
    }

    @Override
    @Transactional
    public void deleteTag(Long id) {
        BlogTag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog tag not found with id: " + id));
        tagRepository.delete(tag);
    }

    private String resolveUniqueSlug(String baseSlug, Long excludeId) {
        String slug = baseSlug;
        int count = 1;
        while ((excludeId == null && blogRepository.existsBySlug(slug)) ||
               (excludeId != null && blogRepository.existsBySlugAndIdNot(slug, excludeId))) {
            slug = baseSlug + "-" + count;
            count++;
        }
        return slug;
    }

    private int calculateReadingTime(String content) {
        if (!StringUtils.hasText(content)) {
            return 1;
        }
        String[] words = content.trim().split("\\s+");
        return Math.max(1, (int) Math.ceil(words.length / 200.0));
    }

    private Set<BlogTag> resolveTags(List<String> tagNames) {
        Set<BlogTag> tags = new HashSet<>();
        if (tagNames == null || tagNames.isEmpty()) {
            return tags;
        }
        for (String tagName : tagNames) {
            String trimmed = tagName.trim();
            if (StringUtils.hasText(trimmed)) {
                BlogTag tag = tagRepository.findByNameIgnoreCase(trimmed)
                        .orElseGet(() -> tagRepository.save(BlogTag.builder()
                                .name(trimmed)
                                .slug(SlugUtils.toSlug(trimmed))
                                .build()));
                tags.add(tag);
            }
        }
        return tags;
    }

    private BlogCategoryDto mapToCategoryDto(BlogCategory cat) {
        if (cat == null) return null;
        return BlogCategoryDto.builder()
                .id(cat.getId())
                .name(cat.getName())
                .slug(cat.getSlug())
                .description(cat.getDescription())
                .build();
    }

    private BlogTagDto mapToTagDto(BlogTag tag) {
        if (tag == null) return null;
        return BlogTagDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .slug(tag.getSlug())
                .build();
    }

    private BlogSummaryDto mapToSummaryDto(Blog blog) {
        List<BlogTagDto> tags = blog.getTags().stream().map(this::mapToTagDto).collect(Collectors.toList());
        return BlogSummaryDto.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .slug(blog.getSlug())
                .excerpt(blog.getExcerpt())
                .coverImageUrl(blog.getCoverImageUrl())
                .author(blog.getAuthor())
                .category(mapToCategoryDto(blog.getCategory()))
                .tags(tags)
                .status(blog.getStatus())
                .publishedAt(blog.getPublishedAt())
                .readingTimeMinutes(blog.getReadingTimeMinutes())
                .createdAt(blog.getCreatedAt())
                .build();
    }

    private BlogDetailDto mapToDetailDto(Blog blog) {
        List<BlogTagDto> tags = blog.getTags().stream().map(this::mapToTagDto).collect(Collectors.toList());
        return BlogDetailDto.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .slug(blog.getSlug())
                .excerpt(blog.getExcerpt())
                .content(blog.getContent())
                .coverImageUrl(blog.getCoverImageUrl())
                .author(blog.getAuthor())
                .category(mapToCategoryDto(blog.getCategory()))
                .tags(tags)
                .status(blog.getStatus())
                .publishedAt(blog.getPublishedAt())
                .readingTimeMinutes(blog.getReadingTimeMinutes())
                .seoTitle(blog.getSeoTitle())
                .seoDescription(blog.getSeoDescription())
                .seoKeywords(blog.getSeoKeywords())
                .createdAt(blog.getCreatedAt())
                .updatedAt(blog.getUpdatedAt())
                .build();
    }
}
