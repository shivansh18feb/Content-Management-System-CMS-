package com.portfolio.cms.media.controller;

import com.portfolio.cms.common.response.ApiResponse;
import com.portfolio.cms.common.response.PagedResponse;
import com.portfolio.cms.media.dto.MediaDto;
import com.portfolio.cms.media.entity.MediaType;
import com.portfolio.cms.media.service.MediaService;
import com.portfolio.cms.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/media")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Admin Media CMS", description = "CMS endpoints for secure image and document uploads and library management")
public class MediaAdminController {

    private final MediaService mediaService;

    @PostMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload Media File", description = "Securely uploads an image or document, validates MIME and extensions, and returns persistent file URL")
    public ResponseEntity<ApiResponse<MediaDto>> uploadMedia(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "altText", required = false) String altText) {
        MediaDto media = mediaService.uploadFile(file, altText, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("File uploaded successfully", media));
    }

    @GetMapping
    @Operation(summary = "List Media Library Files")
    public ResponseEntity<ApiResponse<PagedResponse<MediaDto>>> getMediaList(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) MediaType mediaType,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PagedResponse<MediaDto> list = mediaService.getMediaList(search, mediaType, pageable);
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Media File Metadata by ID")
    public ResponseEntity<ApiResponse<MediaDto>> getMediaById(@PathVariable Long id) {
        MediaDto media = mediaService.getMediaById(id);
        return ResponseEntity.ok(ApiResponse.ok(media));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Media File")
    public ResponseEntity<ApiResponse<Void>> deleteMedia(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        mediaService.deleteMedia(id, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Media file deleted successfully"));
    }
}
