package com.Siddhant.UserApp.Controller;

import com.Siddhant.UserApp.Service.FarmDetailsService;
import com.Siddhant.UserApp.dto.FarmDetailsRequest;
import com.Siddhant.UserApp.dto.FarmDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import com.Siddhant.UserApp.dto.FarmDetailsApiResponse;

@RestController
@RequestMapping("/farm-details")
@RequiredArgsConstructor
@CrossOrigin("*")
public class FarmDetailsController {

    private final FarmDetailsService farmDetailsService;

    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FarmDetailsApiResponse> saveOrUpdateFarmDetails(
            @RequestParam("data") String data,
            @RequestPart(value = "farmerPhoto", required = false) MultipartFile farmerPhoto,
            @RequestPart(value = "farmVideo", required = false) MultipartFile farmVideo) throws IOException {
        
        FarmDetailsResponse response = farmDetailsService.saveOrUpdateFarmDetails(data, farmerPhoto, farmVideo);
        FarmDetailsApiResponse apiResponse = new FarmDetailsApiResponse("Your farm information saved successfully", response);
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping(value = "/update/{farmerId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FarmDetailsApiResponse> updateFarmDetails(
            @PathVariable UUID farmerId,
            @RequestParam("data") String data,
            @RequestPart(value = "farmerPhoto", required = false) MultipartFile farmerPhoto,
            @RequestPart(value = "farmVideo", required = false) MultipartFile farmVideo) throws IOException {
        
        FarmDetailsResponse response = farmDetailsService.updateFarmDetails(farmerId, data, farmerPhoto, farmVideo);
        FarmDetailsApiResponse apiResponse = new FarmDetailsApiResponse("Your farm information updated successfully", response);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/get/{farmerId}")
    public ResponseEntity<FarmDetailsApiResponse> getFarmDetailsByFarmerId(@PathVariable UUID farmerId) {
        FarmDetailsResponse response = farmDetailsService.getFarmDetailsByFarmerId(farmerId);
        FarmDetailsApiResponse apiResponse = new FarmDetailsApiResponse("Farm information fetched successfully", response);
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/delete/{farmerId}")
    public ResponseEntity<FarmDetailsApiResponse> deleteFarmDetails(@PathVariable UUID farmerId) throws IOException {
        farmDetailsService.deleteFarmDetails(farmerId);
        FarmDetailsApiResponse apiResponse = new FarmDetailsApiResponse("Your farm information deleted successfully", null);
        return ResponseEntity.ok(apiResponse);
    }
}
