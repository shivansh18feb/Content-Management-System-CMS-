package com.portfolio.cms.media.service;

import com.portfolio.cms.common.exception.FileStorageException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class LocalStorageServiceImpl implements StorageService {

    @Value("${app.storage.upload-dir:./uploads}")
    private String uploadDir;

    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "webp", "gif", "svg", "pdf"
    ));

    private static final Set<String> ALLOWED_MIME_TYPES = new HashSet<>(Arrays.asList(
            "image/jpeg", "image/png", "image/webp", "image/gif", "image/svg+xml", "application/pdf"
    ));

    private Path rootLocation;

    @PostConstruct
    public void init() {
        try {
            this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(this.rootLocation);
            log.info("Media storage directory initialized at: {}", this.rootLocation);
        } catch (IOException e) {
            throw new FileStorageException("Could not initialize storage directory", e);
        }
    }

    @Override
    public StoredFileInfo store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileStorageException("Cannot upload an empty file");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload");

        // Path Traversal Security Check
        if (originalFilename.contains("..") || originalFilename.contains("/") || originalFilename.contains("\\")) {
            throw new FileStorageException("Filename contains invalid path sequence: " + originalFilename);
        }

        // Extension check
        String extension = getExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new FileStorageException("File extension ." + extension + " is not permitted. Allowed: " + ALLOWED_EXTENSIONS);
        }

        // MIME Type Check
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new FileStorageException("MIME type " + contentType + " is not permitted.");
        }

        // Generate unique UUID storage name
        String storedFilename = UUID.randomUUID().toString() + "." + extension;
        Path destinationFile = this.rootLocation.resolve(storedFilename).normalize().toAbsolutePath();

        if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
            throw new FileStorageException("Cannot store file outside current directory");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file: " + originalFilename, e);
        }

        String fileUrl = "/uploads/" + storedFilename;
        return new StoredFileInfo(
                originalFilename,
                storedFilename,
                destinationFile.toString(),
                fileUrl,
                contentType,
                file.getSize()
        );
    }

    @Override
    public void delete(String storedFilename) {
        try {
            Path file = this.rootLocation.resolve(storedFilename).normalize().toAbsolutePath();
            if (Files.exists(file)) {
                Files.delete(file);
                log.info("Deleted physical media file: {}", file);
            }
        } catch (IOException e) {
            log.error("Failed to delete media file {}: {}", storedFilename, e.getMessage());
        }
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1);
        }
        return "";
    }
}
