package com.Siddhant.UserApp.Controller;

import com.Siddhant.UserApp.Service.FarmGalleryService;
import com.Siddhant.UserApp.dto.FarmGalleryApiResponse;
import com.Siddhant.UserApp.dto.FarmGalleryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/farm-gallery")
@RequiredArgsConstructor
@CrossOrigin("*")
public class FarmGalleryController {

    private final FarmGalleryService farmGalleryService;

    @PostMapping(value = "/upload/{farmerId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FarmGalleryApiResponse> uploadFile(
            @PathVariable UUID farmerId,
            @RequestPart("file") MultipartFile file) {
        try {
            FarmGalleryResponse response = farmGalleryService.uploadFile(farmerId, file);
            return ResponseEntity.ok(new FarmGalleryApiResponse("Farm gallery file uploaded successfully", response));
        } catch (IllegalArgumentException e) {
            // Returns 400 Bad Request
            return ResponseEntity.badRequest().body(new FarmGalleryApiResponse(e.getMessage(), null));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Farmer not found")) {
                // Returns 404 Not Found
                return ResponseEntity.status(404).body(new FarmGalleryApiResponse(e.getMessage(), null));
            }
            // Other generic runtime exceptions (e.g. invalid UUID if spring doesn't catch it first)
            return ResponseEntity.badRequest().body(new FarmGalleryApiResponse(e.getMessage(), null));
        } catch (IOException e) {
            // Returns 500 Internal Server Error for filesystem issues
            return ResponseEntity.status(500).body(new FarmGalleryApiResponse("Failed to save file: " + e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<FarmGalleryApiResponse> getMyGallery() {
        try {
            java.util.List<FarmGalleryResponse> response = farmGalleryService.getMyGallery();
            return ResponseEntity.ok(new FarmGalleryApiResponse("Farm gallery fetched successfully", response));
        } catch (org.springframework.web.server.ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(new FarmGalleryApiResponse(e.getReason(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new FarmGalleryApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<FarmGalleryApiResponse> getGalleryForCustomer(@PathVariable UUID farmerId) {
        try {
            java.util.List<FarmGalleryResponse> response = farmGalleryService.getGalleryByFarmerId(farmerId);
            return ResponseEntity.ok(new FarmGalleryApiResponse("Farmer gallery fetched successfully", response));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Farmer not found")) {
                return ResponseEntity.status(404).body(new FarmGalleryApiResponse(e.getMessage(), null));
            }
            return ResponseEntity.badRequest().body(new FarmGalleryApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping(value = "/update/{galleryId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FarmGalleryApiResponse> updateFile(
            @PathVariable UUID galleryId,
            @RequestPart("file") MultipartFile file) {
        try {
            FarmGalleryResponse response = farmGalleryService.updateFile(galleryId, file);
            return ResponseEntity.ok(new FarmGalleryApiResponse("Farm gallery file updated successfully", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new FarmGalleryApiResponse(e.getMessage(), null));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Gallery record not found")) {
                return ResponseEntity.status(404).body(new FarmGalleryApiResponse(e.getMessage(), null));
            }
            return ResponseEntity.badRequest().body(new FarmGalleryApiResponse(e.getMessage(), null));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(new FarmGalleryApiResponse("Failed to update file: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/delete/{galleryId}")
    public ResponseEntity<FarmGalleryApiResponse> deleteFile(@PathVariable UUID galleryId) {
        try {
            farmGalleryService.deleteFile(galleryId);
            return ResponseEntity.ok(new FarmGalleryApiResponse("Farm gallery file deleted successfully", null));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Gallery record not found")) {
                return ResponseEntity.status(404).body(new FarmGalleryApiResponse(e.getMessage(), null));
            }
            return ResponseEntity.badRequest().body(new FarmGalleryApiResponse(e.getMessage(), null));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(new FarmGalleryApiResponse("Failed to delete file: " + e.getMessage(), null));
        }
    }
}
