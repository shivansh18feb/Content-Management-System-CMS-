package com.portfolio.cms.media.service;

import com.portfolio.cms.common.response.PagedResponse;
import com.portfolio.cms.media.dto.MediaDto;
import com.portfolio.cms.media.entity.MediaType;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface MediaService {
    MediaDto uploadFile(MultipartFile file, String altText, Long userId, String userEmail);
    PagedResponse<MediaDto> getMediaList(String search, MediaType mediaType, Pageable pageable);
    MediaDto getMediaById(Long id);
    void deleteMedia(Long id, Long userId, String userEmail);
}
