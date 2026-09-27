package com.portfolio.cms.blog.dto;

import com.portfolio.cms.blog.entity.BlogStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogDetailDto {
    private Long id;
    private String title;
    private String slug;
    private String excerpt;
    private String content;
    private String coverImageUrl;
    private String author;
    private BlogCategoryDto category;
    private List<BlogTagDto> tags;
    private BlogStatus status;
    private Instant publishedAt;
    private int readingTimeMinutes;
    private String seoTitle;
    private String seoDescription;
    private String seoKeywords;
    private Instant createdAt;
    private Instant updatedAt;
}
