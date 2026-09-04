package com.Siddhant.UserApp.Service;

import com.Siddhant.UserApp.dto.FarmGalleryResponse;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

import java.util.List;

public interface FarmGalleryService {
    FarmGalleryResponse uploadFile(UUID farmerId, MultipartFile file) throws IOException;
    List<FarmGalleryResponse> getMyGallery();
    List<FarmGalleryResponse> getGalleryByFarmerId(UUID farmerId);
    FarmGalleryResponse updateFile(UUID galleryId, MultipartFile file) throws IOException;
    void deleteFile(UUID galleryId) throws IOException;
}
