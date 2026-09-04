package com.Siddhant.UserApp.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
@Service
public class FarmFileStorageService {
    private static final Logger log = LoggerFactory.getLogger(FarmFileStorageService.class);
    private static final String UPLOAD_DIR = "uploads/farmers/";
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("image/jpeg", "image/png", "image/jpg", "image/webp");
    private static final List<String> ALLOWED_IMAGE_EXTS = Arrays.asList(".jpg", ".jpeg", ".png", ".webp");
    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList("video/mp4", "video/quicktime", "video/webm");
    private static final List<String> ALLOWED_VIDEO_EXTS = Arrays.asList(".mp4", ".mov", ".webm");
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5 MB
    private static final long MAX_VIDEO_SIZE = 50 * 1024 * 1024; // 50 MB
    public String saveFarmerPhoto(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;
        log.info("Uploaded file: {}, Content-Type: {}, Size: {}",
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize());
        validateFile(file, ALLOWED_IMAGE_TYPES, ALLOWED_IMAGE_EXTS, MAX_IMAGE_SIZE, "Image");
        return saveFile(file);
    }public String saveFarmVideo(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;
        log.info("Uploaded file: {}, Content-Type: {}, Size: {}",
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize());
        validateFile(file, ALLOWED_VIDEO_TYPES, ALLOWED_VIDEO_EXTS, MAX_VIDEO_SIZE, "Video");
        return saveFile(file);
    }public String getFileType(MultipartFile file) {
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        boolean isImageMime = contentType != null && ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase());
        boolean isVideoMime = contentType != null && ALLOWED_VIDEO_TYPES.contains(contentType.toLowerCase());
        boolean isImageExt = false;
        boolean isVideoExt = false;
        if (originalFilename != null && originalFilename.contains(".")) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            isImageExt = ALLOWED_IMAGE_EXTS.contains(extension);
            isVideoExt = ALLOWED_VIDEO_EXTS.contains(extension);
        }if (isImageMime || isImageExt) return "IMAGE";
        if (isVideoMime || isVideoExt) return "VIDEO";
        throw new IllegalArgumentException("Invalid file type. Only JPG, JPEG, PNG, WEBP, MP4, MOV and WEBM files are allowed");
    }public String saveGalleryFile(MultipartFile file, String fileType) throws IOException {
        if ("IMAGE".equals(fileType)) {
            validateFile(file, ALLOWED_IMAGE_TYPES, ALLOWED_IMAGE_EXTS, MAX_IMAGE_SIZE, "Image");
        } else if ("VIDEO".equals(fileType)) {
            validateFile(file, ALLOWED_VIDEO_TYPES, ALLOWED_VIDEO_EXTS, MAX_VIDEO_SIZE, "Video");
        }return saveFile(file);
    }private void validateFile(MultipartFile file, List<String> allowedTypes, List<String> allowedExts, long maxSize, String fileType) {
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        boolean isValidMime = contentType != null && allowedTypes.contains(contentType.toLowerCase());
        boolean isValidExt = false;
        if (originalFilename != null && originalFilename.contains(".")) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            isValidExt = allowedExts.contains(extension);
        }if (!isValidMime && !isValidExt) {
            throw new IllegalArgumentException("Invalid " + fileType + " type. Allowed extensions are: " + allowedExts);
        }if (file.getSize() > maxSize) {
            throw new IllegalArgumentException(fileType + " size exceeds the maximum limit of " + (maxSize / (1024 * 1024)) + "MB.");
        }
    }private String saveFile(MultipartFile file) throws IOException {
        File folder = new File(UPLOAD_DIR);
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            log.info("Upload folder created at {}: {}", UPLOAD_DIR, created);
        }String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }String fileName = UUID.randomUUID().toString() + extension;
        File destination = new File(folder, fileName);
        log.debug("Saving file to: {}", destination.getAbsolutePath());
        try (FileOutputStream fos = new FileOutputStream(destination)) {
            fos.write(file.getBytes());
        }return UPLOAD_DIR + fileName; // Return the path to store in DB
    }public void deleteFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return;
        }if (filePath.contains("..") || !filePath.startsWith(UPLOAD_DIR)) {
            log.warn("Attempt to delete a file outside the allowed directory: {}", filePath);
            return;
        }File file = new File(filePath);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                log.info("Successfully deleted file: {}", filePath);
            } else {
                log.error("Failed to delete file: {}. Check filesystem permissions.", filePath);
            }
        } else {
            log.warn("File not found for deletion, skipping: {}", filePath);
        }
    }
}
