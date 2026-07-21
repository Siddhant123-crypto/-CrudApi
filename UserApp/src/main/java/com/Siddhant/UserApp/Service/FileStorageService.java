package com.Siddhant.UserApp.Service;

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

    @Value("${file.upload-dir}")
    private String uploadDir;

    // Save File
    public String saveFile(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            System.out.println("File is Empty");
            return null;
        }

        System.out.println("========================================");
        System.out.println("Original File Name : " + file.getOriginalFilename());
        System.out.println("File Size          : " + file.getSize());
        System.out.println("Content Type       : " + file.getContentType());
        System.out.println("Upload Dir         : " + uploadDir);

        File folder = new File(uploadDir);

        if (!folder.exists()) {

            boolean created = folder.mkdirs();

            System.out.println("Folder Created : " + created);
        }

        System.out.println("Folder Exists  : " + folder.exists());
        System.out.println("Upload Folder  : " + folder.getAbsolutePath());

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        File destination = new File(folder, fileName);

        System.out.println("Saving To      : " + destination.getAbsolutePath());

        FileOutputStream fos = new FileOutputStream(destination);
        fos.write(file.getBytes());
        fos.close();

        System.out.println("File Exists    : " + destination.exists());
        System.out.println("File Saved Successfully");
        System.out.println("========================================");

        return fileName;
    }

    // Read File
    public byte[] getFile(String fileName) {

        try {

            Path path = Paths.get(uploadDir, fileName);

            return Files.readAllBytes(path);

        } catch (Exception e) {

            e.printStackTrace();

            return null;

        }

    }

}