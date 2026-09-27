package com.portfolio.cms.about.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AboutDto {

    private Long id;

    @NotBlank(message = "Headline cannot be blank")
    private String headline;

    @NotBlank(message = "Short bio cannot be blank")
    private String shortBio;

    @NotBlank(message = "Long bio cannot be blank")
    private String longBio;

    private String profileImageUrl;
    private String location;
    private String email;
    private String phone;
    private String resumeUrl;
    private String availability;
    private int yearsOfExperience;
    private String githubUrl;
    private String linkedinUrl;
    private String twitterUrl;
}
