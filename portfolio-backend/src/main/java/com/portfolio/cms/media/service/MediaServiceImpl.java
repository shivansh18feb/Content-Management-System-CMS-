package com.portfolio.cms.media.service;

import com.portfolio.cms.audit.service.AuditLogService;
import com.portfolio.cms.common.exception.ResourceNotFoundException;
import com.portfolio.cms.common.response.PagedResponse;
import com.portfolio.cms.media.dto.MediaDto;
import com.portfolio.cms.media.entity.Media;
import com.portfolio.cms.media.entity.MediaType;
import com.portfolio.cms.media.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;
    private final StorageService storageService;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public MediaDto uploadFile(MultipartFile file, String altText, Long userId, String userEmail) {
        StorageService.StoredFileInfo fileInfo = storageService.store(file);

        MediaType mediaType = MediaType.IMAGE;
        if (fileInfo.contentType().startsWith("application/pdf")) {
            mediaType = MediaType.DOCUMENT;
        }

        Media media = Media.builder()
                .originalFilename(fileInfo.originalFilename())
                .storedFilename(fileInfo.storedFilename())
                .filePath(fileInfo.filePath())
                .fileUrl(fileInfo.fileUrl())
                .contentType(fileInfo.contentType())
                .fileSize(fileInfo.fileSize())
                .mediaType(mediaType)
                .altText(altText)
                .createdBy(userEmail)
                .build();

        Media saved = mediaRepository.save(media);
        auditLogService.log(userId, userEmail, "UPLOAD", "Media", String.valueOf(saved.getId()), null, "Uploaded media file: " + saved.getOriginalFilename());
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<MediaDto> getMediaList(String search, MediaType mediaType, Pageable pageable) {
        Pageable snakePageable = com.portfolio.cms.common.util.PageableUtils.toSnakeCase(pageable);
        String typeStr = mediaType != null ? mediaType.name() : null;
        Page<Media> page = mediaRepository.findWithFilters(search, typeStr, snakePageable);
        return PagedResponse.of(page.map(this::mapToDto));
    }

    @Override
    @Transactional(readOnly = true)
    public MediaDto getMediaById(Long id) {
        Media media = mediaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Media not found with id: " + id));
        return mapToDto(media);
    }

    @Override
    @Transactional
    public void deleteMedia(Long id, Long userId, String userEmail) {
        Media media = mediaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Media not found with id: " + id));

        storageService.delete(media.getStoredFilename());
        String originalFilename = media.getOriginalFilename();
        mediaRepository.delete(media);

        auditLogService.log(userId, userEmail, "DELETE", "Media", String.valueOf(id), null, "Deleted media: " + originalFilename);
    }

    private MediaDto mapToDto(Media m) {
        return MediaDto.builder()
                .id(m.getId())
                .originalFilename(m.getOriginalFilename())
                .storedFilename(m.getStoredFilename())
                .fileUrl(m.getFileUrl())
                .contentType(m.getContentType())
                .fileSize(m.getFileSize())
                .mediaType(m.getMediaType())
                .altText(m.getAltText())
                .createdAt(m.getCreatedAt())
                .createdBy(m.getCreatedBy())
                .build();
    }
}
