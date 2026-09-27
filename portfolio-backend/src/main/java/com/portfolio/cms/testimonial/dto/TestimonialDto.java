package com.portfolio.cms.testimonial.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestimonialDto {
    private Long id;
    private String name;
    private String role;
    private String company;
    private String avatarUrl;
    private String content;
    private int rating;
    private boolean featured;
    private boolean published;
    private int displayOrder;
}
