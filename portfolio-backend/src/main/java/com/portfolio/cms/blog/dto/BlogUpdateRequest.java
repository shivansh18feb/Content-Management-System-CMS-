package com.portfolio.cms.blog.dto;

import com.portfolio.cms.blog.entity.BlogStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogUpdateRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String slug;

    @NotBlank(message = "Excerpt is required")
    private String excerpt;

    @NotBlank(message = "Content is required")
    private String content;

    private String coverImageUrl;
    private String author;
    private Long categoryId;
    private List<String> tagNames;
    private BlogStatus status;
    private String seoTitle;
    private String seoDescription;
    private String seoKeywords;
}
