package com.portfolio.cms.blog.controller;

import com.portfolio.cms.blog.dto.BlogCategoryDto;
import com.portfolio.cms.blog.dto.BlogDetailDto;
import com.portfolio.cms.blog.dto.BlogSummaryDto;
import com.portfolio.cms.blog.dto.BlogTagDto;
import com.portfolio.cms.blog.service.BlogService;
import com.portfolio.cms.common.response.ApiResponse;
import com.portfolio.cms.common.response.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/blogs")
@RequiredArgsConstructor
@Tag(name = "Public Blog", description = "Public endpoints for technical articles, tutorials, and category feeds")
public class BlogPublicController {

    private final BlogService blogService;

    @GetMapping
    @Operation(summary = "List Published Articles", description = "Returns only published blog articles with category, tag, and search filters")
    public ResponseEntity<ApiResponse<PagedResponse<BlogSummaryDto>>> getBlogs(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag,
            @PageableDefault(size = 9) Pageable pageable) {
        PagedResponse<BlogSummaryDto> response = blogService.getPublishedBlogs(search, category, tag, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get Article by Slug", description = "Returns full markdown content, reading time, and metadata for published article")
    public ResponseEntity<ApiResponse<BlogDetailDto>> getBlogBySlug(@PathVariable String slug) {
        BlogDetailDto blog = blogService.getPublishedBlogBySlug(slug);
        return ResponseEntity.ok(ApiResponse.ok(blog));
    }

    @GetMapping("/categories")
    @Operation(summary = "List Blog Categories")
    public ResponseEntity<ApiResponse<List<BlogCategoryDto>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.ok(blogService.getAllCategories()));
    }

    @GetMapping("/tags")
    @Operation(summary = "List Blog Tags")
    public ResponseEntity<ApiResponse<List<BlogTagDto>>> getTags() {
        return ResponseEntity.ok(ApiResponse.ok(blogService.getAllTags()));
    }
}
