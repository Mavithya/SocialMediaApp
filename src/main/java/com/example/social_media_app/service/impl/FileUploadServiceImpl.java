package com.example.social_media_app.service.impl;

import com.example.social_media_app.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${app.upload.dir:src/main/resources/static/uploads}")
    private String uploadDir;

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
            "video/mp4", "video/avi", "video/mov", "video/wmv", "video/webm"
    );

    private static final String FILE_TYPE_VIDEO = "video";
    private static final String FILE_TYPE_IMAGE = "image";
    private static final String UPLOADS_PATH = "/uploads/";
    
    private static final long MAX_IMAGE_SIZE = 10L * 1024 * 1024; // 10MB for images
    private static final long MAX_VIDEO_SIZE = 50L * 1024 * 1024; // 50MB for videos

    @Override
    public String uploadFile(MultipartFile file, String directory) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Cannot upload empty file");
        }

        if (!isValidFileType(file)) {
            throw new IOException("File type not allowed: " + file.getContentType());
        }

        // Check file size based on type
        String fileTypeCategory = getFileTypeCategory(file);
        long maxSize = FILE_TYPE_VIDEO.equals(fileTypeCategory) ? MAX_VIDEO_SIZE : MAX_IMAGE_SIZE;
        
        if (file.getSize() > maxSize) {
            String maxSizeStr = FILE_TYPE_VIDEO.equals(fileTypeCategory) ? "50MB" : "10MB";
            throw new IOException("File size exceeds maximum allowed size of " + maxSizeStr);
        }

        // Create directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir, directory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        // Save file
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        log.info("File uploaded successfully: {}", filePath);

        // Return path relative to static resources
        return UPLOADS_PATH + directory + "/" + uniqueFilename;
    }

    @Override
    public List<String> uploadFiles(List<MultipartFile> files, String directory) throws IOException {
        List<String> uploadedPaths = new ArrayList<>();
        
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String filePath = uploadFile(file, directory);
                uploadedPaths.add(filePath);
            }
        }
        
        return uploadedPaths;
    }

    @Override
    public boolean deleteFile(String filePath) {
        try {
            // Remove the leading slash and "uploads/" to get the path relative to upload dir
            String relativePath = filePath.startsWith(UPLOADS_PATH) ? 
                    filePath.substring(UPLOADS_PATH.length()) : filePath;
            
            Path fileToDelete = Paths.get(uploadDir, relativePath);
            boolean deleted = Files.deleteIfExists(fileToDelete);
            
            if (deleted) {
                log.info("File deleted successfully: {}", fileToDelete);
            } else {
                log.warn("File not found for deletion: {}", fileToDelete);
            }
            
            return deleted;
        } catch (IOException e) {
            log.error("Error deleting file: {}", filePath, e);
            return false;
        }
    }

    @Override
    public boolean isValidFileType(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && 
               (ALLOWED_IMAGE_TYPES.contains(contentType) || ALLOWED_VIDEO_TYPES.contains(contentType));
    }

    @Override
    public String getFileTypeCategory(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) {
            return "unknown";
        }
        
        if (ALLOWED_IMAGE_TYPES.contains(contentType)) {
            return FILE_TYPE_IMAGE;
        } else if (ALLOWED_VIDEO_TYPES.contains(contentType)) {
            return FILE_TYPE_VIDEO;
        } else {
            return "unknown";
        }
    }
}
