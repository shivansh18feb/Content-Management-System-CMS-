package com.portfolio.cms.blog.service;

import com.portfolio.cms.blog.dto.*;
import com.portfolio.cms.blog.entity.BlogStatus;
import com.portfolio.cms.common.response.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BlogService {
    PagedResponse<BlogSummaryDto> getPublishedBlogs(String search, String categorySlug, String tagSlug, Pageable pageable);
    BlogDetailDto getPublishedBlogBySlug(String slug);
    List<BlogCategoryDto> getAllCategories();
    List<BlogTagDto> getAllTags();

    PagedResponse<BlogSummaryDto> getAdminBlogs(String search, Long categoryId, BlogStatus status, Pageable pageable);
    BlogDetailDto getBlogById(Long id);
    BlogDetailDto createBlog(BlogCreateRequest request, Long userId, String userEmail);
    BlogDetailDto updateBlog(Long id, BlogUpdateRequest request, Long userId, String userEmail);
    void deleteBlog(Long id, Long userId, String userEmail);

    BlogDetailDto publishBlog(Long id, Long userId, String userEmail);
    BlogDetailDto unpublishBlog(Long id, Long userId, String userEmail);
    BlogDetailDto archiveBlog(Long id, Long userId, String userEmail);

    BlogCategoryDto createCategory(CreateBlogCategoryRequest request);
    void deleteCategory(Long id);
    BlogTagDto createTag(CreateBlogTagRequest request);
    void deleteTag(Long id);
}
