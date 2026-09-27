package com.portfolio.cms.media.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    StoredFileInfo store(MultipartFile file);
    void delete(String storedFilename);

    record StoredFileInfo(String originalFilename, String storedFilename, String filePath, String fileUrl, String contentType, long fileSize) {}
}
