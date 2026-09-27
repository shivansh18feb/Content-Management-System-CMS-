package com.portfolio.cms.testimonial.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestimonialRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Role is required")
    private String role;

    private String company;
    private String avatarUrl;

    @NotBlank(message = "Content is required")
    private String content;

    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    @Builder.Default
    private int rating = 5;

    @Builder.Default
    private boolean featured = false;

    @Builder.Default
    private boolean published = true;

    @Builder.Default
    private int displayOrder = 0;
}
