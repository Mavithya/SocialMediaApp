package com.example.social_media_app.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileUploadService {
    
    /**
     * Upload a single file
     * @param file The file to upload
     * @param directory The directory to upload to (relative to upload root)
     * @return The file path relative to the static resources
     */
    String uploadFile(MultipartFile file, String directory) throws IOException;
    
    /**
     * Upload multiple files
     * @param files The files to upload
     * @param directory The directory to upload to (relative to upload root)
     * @return List of file paths relative to the static resources
     */
    List<String> uploadFiles(List<MultipartFile> files, String directory) throws IOException;
    
    /**
     * Delete a file
     * @param filePath The file path relative to the static resources
     * @return true if deleted successfully
     */
    boolean deleteFile(String filePath);
    
    /**
     * Check if file type is allowed
     * @param file The file to check
     * @return true if file type is allowed
     */
    boolean isValidFileType(MultipartFile file);
    
    /**
     * Get file type category (image/video)
     * @param file The file to check
     * @return "image" or "video" or "unknown"
     */
    String getFileTypeCategory(MultipartFile file);
}
