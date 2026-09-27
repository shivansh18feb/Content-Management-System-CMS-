package com.portfolio.cms.blog.controller;

import com.portfolio.cms.blog.dto.*;
import com.portfolio.cms.blog.entity.BlogStatus;
import com.portfolio.cms.blog.service.BlogService;
import com.portfolio.cms.common.response.ApiResponse;
import com.portfolio.cms.common.response.PagedResponse;
import com.portfolio.cms.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/blogs")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Admin Blogs CMS", description = "CMS endpoints for article drafting, authoring, publishing, and archiving")
public class BlogAdminController {

    private final BlogService blogService;

    @GetMapping
    @Operation(summary = "List Articles for Admin with Status Filter")
    public ResponseEntity<ApiResponse<PagedResponse<BlogSummaryDto>>> getBlogs(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BlogStatus status,
            @PageableDefault(size = 15) Pageable pageable) {
        PagedResponse<BlogSummaryDto> response = blogService.getAdminBlogs(search, categoryId, status, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Article by ID")
    public ResponseEntity<ApiResponse<BlogDetailDto>> getBlogById(@PathVariable Long id) {
        BlogDetailDto blog = blogService.getBlogById(id);
        return ResponseEntity.ok(ApiResponse.ok(blog));
    }

    @PostMapping
    @Operation(summary = "Create Article (Draft or Published)")
    public ResponseEntity<ApiResponse<BlogDetailDto>> createBlog(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody BlogCreateRequest request) {
        BlogDetailDto created = blogService.createBlog(request, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Article created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Article")
    public ResponseEntity<ApiResponse<BlogDetailDto>> updateBlog(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody BlogUpdateRequest request) {
        BlogDetailDto updated = blogService.updateBlog(id, request, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Article updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Article")
    public ResponseEntity<ApiResponse<Void>> deleteBlog(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        blogService.deleteBlog(id, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Article deleted successfully"));
    }

    @RequestMapping(value = "/{id}/publish", method = {RequestMethod.POST, RequestMethod.PATCH})
    @Operation(summary = "Publish Article")
    public ResponseEntity<ApiResponse<BlogDetailDto>> publishBlog(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        BlogDetailDto published = blogService.publishBlog(id, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Article published successfully", published));
    }

    @RequestMapping(value = "/{id}/unpublish", method = {RequestMethod.POST, RequestMethod.PATCH})
    @Operation(summary = "Unpublish Article (Revert to Draft)")
    public ResponseEntity<ApiResponse<BlogDetailDto>> unpublishBlog(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        BlogDetailDto unpublished = blogService.unpublishBlog(id, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Article unpublished (reverted to draft)", unpublished));
    }

    @RequestMapping(value = "/{id}/archive", method = {RequestMethod.POST, RequestMethod.PATCH})
    @Operation(summary = "Archive Article")
    public ResponseEntity<ApiResponse<BlogDetailDto>> archiveBlog(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        BlogDetailDto archived = blogService.archiveBlog(id, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Article archived", archived));
    }

    @GetMapping("/categories")
    @Operation(summary = "List Categories")
    public ResponseEntity<ApiResponse<List<BlogCategoryDto>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.ok(blogService.getAllCategories()));
    }

    @PostMapping("/categories")
    @Operation(summary = "Create Category")
    public ResponseEntity<ApiResponse<BlogCategoryDto>> createCategory(@Valid @RequestBody CreateBlogCategoryRequest request) {
        BlogCategoryDto created = blogService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Category created", created));
    }

    @DeleteMapping("/categories/{id}")
    @Operation(summary = "Delete Category")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        blogService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.ok("Category deleted"));
    }

    @GetMapping("/tags")
    @Operation(summary = "List Tags")
    public ResponseEntity<ApiResponse<List<BlogTagDto>>> getTags() {
        return ResponseEntity.ok(ApiResponse.ok(blogService.getAllTags()));
    }

    @PostMapping("/tags")
    @Operation(summary = "Create Tag")
    public ResponseEntity<ApiResponse<BlogTagDto>> createTag(@Valid @RequestBody CreateBlogTagRequest request) {
        BlogTagDto created = blogService.createTag(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Tag created", created));
    }

    @DeleteMapping("/tags/{id}")
    @Operation(summary = "Delete Tag")
    public ResponseEntity<ApiResponse<Void>> deleteTag(@PathVariable Long id) {
        blogService.deleteTag(id);
        return ResponseEntity.ok(ApiResponse.ok("Tag deleted"));
    }
}
