package com.Siddhant.UserApp.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
@Service
public class FileStorageService {
    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);
    @Value("${file.upload-dir}")
    private String uploadDir;
    public String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            log.warn("Attempted to save an empty file.");
            return null;
        }
        log.debug("Original File Name : {}, Size: {}, Content Type: {}", 
                file.getOriginalFilename(), file.getSize(), file.getContentType());
        File folder = new File(uploadDir);
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            log.info("Upload folder created: {}", created);
        }
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File destination = new File(folder, fileName);
        log.debug("Saving file to: {}", destination.getAbsolutePath());
        try (FileOutputStream fos = new FileOutputStream(destination)) {
            fos.write(file.getBytes());
        }
        log.info("File {} saved successfully", fileName);
        return fileName;
    }
    public byte[] getFile(String fileName) {
        try {
            Path path = Paths.get(uploadDir, fileName);
            if (!Files.exists(path)) {
                log.warn("File not found: {}", fileName);
                return null;
            }
            return Files.readAllBytes(path);
        } catch (IOException e) {
            log.error("Error reading file: {}", fileName, e);
            return null;
        }
    }
}