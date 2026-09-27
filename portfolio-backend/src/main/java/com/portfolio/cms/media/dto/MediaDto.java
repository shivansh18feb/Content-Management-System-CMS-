package com.portfolio.cms.media.dto;

import com.portfolio.cms.media.entity.MediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaDto {
    private Long id;
    private String originalFilename;
    private String storedFilename;
    private String fileUrl;
    private String contentType;
    private Long fileSize;
    private MediaType mediaType;
    private String altText;
    private Instant createdAt;
    private String createdBy;
}
