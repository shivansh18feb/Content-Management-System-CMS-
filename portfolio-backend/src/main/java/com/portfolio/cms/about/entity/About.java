package com.portfolio.cms.about.entity;

import com.portfolio.cms.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "about")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class About extends BaseEntity {

    @Column(nullable = false)
    private String headline;

    @Column(name = "short_bio", nullable = false, columnDefinition = "TEXT")
    private String shortBio;

    @Column(name = "long_bio", nullable = false, columnDefinition = "TEXT")
    private String longBio;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column
    private String location;

    @Column
    private String email;

    @Column
    private String phone;

    @Column(name = "resume_url")
    private String resumeUrl;

    @Column(nullable = false)
    @Builder.Default
    private String availability = "AVAILABLE";

    @Column(name = "years_of_experience", nullable = false)
    @Builder.Default
    private int yearsOfExperience = 0;

    @Column(name = "github_url")
    private String githubUrl;

    @Column(name = "linkedin_url")
    private String linkedinUrl;

    @Column(name = "twitter_url")
    private String twitterUrl;
}
