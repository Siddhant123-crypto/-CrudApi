package com.Siddhant.UserApp.Service.Impl;

import com.Siddhant.UserApp.Entity.FarmGallery;
import com.Siddhant.UserApp.Entity.FarmerProfile;
import com.Siddhant.UserApp.Repository.FarmGalleryRepository;
import com.Siddhant.UserApp.Repository.FarmerProfileRepository;
import com.Siddhant.UserApp.Service.FarmFileStorageService;
import com.Siddhant.UserApp.Service.FarmGalleryService;
import com.Siddhant.UserApp.dto.FarmGalleryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FarmGalleryServiceImpl implements FarmGalleryService {

    private final FarmGalleryRepository farmGalleryRepository;
    private final FarmerProfileRepository farmerProfileRepository;
    private final FarmFileStorageService farmFileStorageService;

    @Override
    public FarmGalleryResponse uploadFile(UUID farmerId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please upload a file");
        }

        FarmerProfile farmerProfile = farmerProfileRepository.findById(farmerId)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        String fileType = farmFileStorageService.getFileType(file);
        String filePath = farmFileStorageService.saveGalleryFile(file, fileType);

        FarmGallery farmGallery = new FarmGallery();
        farmGallery.setFarmer(farmerProfile);
        farmGallery.setFilePath(filePath);
        farmGallery.setFileType(fileType);
        farmGallery.setOriginalFileName(file.getOriginalFilename());

        FarmGallery savedGallery = farmGalleryRepository.save(farmGallery);

        return mapToResponse(savedGallery);
    }

    @Override
    public java.util.List<FarmGalleryResponse> getMyGallery() {
        FarmerProfile farmer = getLoggedInFarmer();
        return farmGalleryRepository.findByFarmerIdOrderByCreatedAtDesc(farmer.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public java.util.List<FarmGalleryResponse> getGalleryByFarmerId(UUID farmerId) {
        if (!farmerProfileRepository.existsById(farmerId)) {
            throw new RuntimeException("Farmer not found");
        }
        return farmGalleryRepository.findByFarmerIdOrderByCreatedAtDesc(farmerId)
                .stream()
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    private FarmerProfile getLoggedInFarmer() {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        return farmerProfileRepository.findByUser_Email(authentication.getName())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Farmer profile not found"));
    }

    @Override
    public FarmGalleryResponse updateFile(UUID galleryId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please upload a file");
        }

        FarmGallery farmGallery = farmGalleryRepository.findById(galleryId)
                .orElseThrow(() -> new RuntimeException("Gallery record not found"));

        // Validate and save the new file
        String newFileType = farmFileStorageService.getFileType(file);
        String newFilePath = farmFileStorageService.saveGalleryFile(file, newFileType);

        // Delete the old file only after the new one is saved successfully
        farmFileStorageService.deleteFile(farmGallery.getFilePath());

        // Update database record
        farmGallery.setFilePath(newFilePath);
        farmGallery.setFileType(newFileType);
        farmGallery.setOriginalFileName(file.getOriginalFilename());

        FarmGallery updatedGallery = farmGalleryRepository.save(farmGallery);
        return mapToResponse(updatedGallery);
    }

    @Override
    public void deleteFile(UUID galleryId) throws IOException {
        FarmGallery farmGallery = farmGalleryRepository.findById(galleryId)
                .orElseThrow(() -> new RuntimeException("Gallery record not found"));

        // Delete physical file
        farmFileStorageService.deleteFile(farmGallery.getFilePath());

        // Delete database record
        farmGalleryRepository.delete(farmGallery);
    }

    private FarmGalleryResponse mapToResponse(FarmGallery gallery) {
        return new FarmGalleryResponse(
                gallery.getId(),
                gallery.getFarmer().getId(),
                gallery.getFilePath(),
                gallery.getFileType(),
                gallery.getOriginalFileName(),
                gallery.getCreatedAt()
        );
    }
}
