package com.Siddhant.UserApp.Service.Impl;

import com.Siddhant.UserApp.Entity.FarmDetails;
import com.Siddhant.UserApp.Entity.FarmerProfile;
import com.Siddhant.UserApp.Repository.FarmDetailsRepository;
import com.Siddhant.UserApp.Repository.FarmerProfileRepository;
import com.Siddhant.UserApp.Service.FarmDetailsService;
import com.Siddhant.UserApp.Service.FarmFileStorageService;
import com.Siddhant.UserApp.dto.FarmDetailsRequest;
import com.Siddhant.UserApp.dto.FarmDetailsResponse;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FarmDetailsServiceImpl implements FarmDetailsService {

    private final FarmDetailsRepository farmDetailsRepository;
    private final FarmerProfileRepository farmerProfileRepository;
    private final FarmFileStorageService farmFileStorageService;
    private final ObjectMapper objectMapper;

    @Override
    public FarmDetailsResponse saveOrUpdateFarmDetails(String requestData, MultipartFile farmerPhoto, MultipartFile farmVideo) throws IOException {
        FarmDetailsRequest request = objectMapper.readValue(requestData, FarmDetailsRequest.class);
        
        FarmerProfile farmerProfile = farmerProfileRepository.findById(request.getFarmerId())
                .orElseThrow(() -> new RuntimeException("Farmer Profile not found"));

        Optional<FarmDetails> existingFarmDetailsOpt = farmDetailsRepository.findByFarmerId(request.getFarmerId());
        FarmDetails farmDetails;

        if (existingFarmDetailsOpt.isPresent()) {
            farmDetails = existingFarmDetailsOpt.get();
        } else {
            farmDetails = new FarmDetails();
            farmDetails.setFarmer(farmerProfile);
        }

        farmDetails.setFarmName(request.getFarmName());
        farmDetails.setTagline(request.getTagline());
        farmDetails.setLocation(request.getLocation());
        farmDetails.setFarmingSince(request.getFarmingSince());
        farmDetails.setFarmingType(request.getFarmingType());
        farmDetails.setFarmArea(request.getFarmArea());
        farmDetails.setFarmAreaUnit(request.getFarmAreaUnit());
        farmDetails.setCropsGrownCount(request.getCropsGrownCount());
        farmDetails.setYearsExperience(request.getYearsExperience());
        farmDetails.setHappyCustomersCount(request.getHappyCustomersCount());
        farmDetails.setFarmerName(request.getFarmerName());
        farmDetails.setMainCrops(request.getMainCrops());
        farmDetails.setIrrigationSource(request.getIrrigationSource());
        farmDetails.setSoilType(request.getSoilType());
        farmDetails.setAboutFarm(request.getAboutFarm());

        // File handling
        if (farmerPhoto != null && !farmerPhoto.isEmpty()) {
            String photoPath = farmFileStorageService.saveFarmerPhoto(farmerPhoto);
            farmDetails.setFarmerPhoto(photoPath);
        }

        if (farmVideo != null && !farmVideo.isEmpty()) {
            String videoPath = farmFileStorageService.saveFarmVideo(farmVideo);
            farmDetails.setFarmVideo(videoPath);
        }

        FarmDetails savedFarmDetails = farmDetailsRepository.save(farmDetails);
        return mapToResponse(savedFarmDetails);
    }

    @Override
    public FarmDetailsResponse updateFarmDetails(UUID farmerId, String requestData, MultipartFile farmerPhoto, MultipartFile farmVideo) throws IOException {
        FarmDetailsRequest request = objectMapper.readValue(requestData, FarmDetailsRequest.class);

        FarmDetails farmDetails = farmDetailsRepository.findByFarmerId(farmerId)
                .orElseThrow(() -> new RuntimeException("Farm Details not found for farmer id: " + farmerId));

        // Update fields (excluding id, farmerId, verified, verifiedOn, certificateFile)
        if (request.getFarmName() != null) farmDetails.setFarmName(request.getFarmName());
        if (request.getTagline() != null) farmDetails.setTagline(request.getTagline());
        if (request.getLocation() != null) farmDetails.setLocation(request.getLocation());
        if (request.getFarmingSince() != null) farmDetails.setFarmingSince(request.getFarmingSince());
        if (request.getFarmingType() != null) farmDetails.setFarmingType(request.getFarmingType());
        if (request.getFarmArea() != null) farmDetails.setFarmArea(request.getFarmArea());
        if (request.getFarmAreaUnit() != null) farmDetails.setFarmAreaUnit(request.getFarmAreaUnit());
        if (request.getCropsGrownCount() != null) farmDetails.setCropsGrownCount(request.getCropsGrownCount());
        if (request.getYearsExperience() != null) farmDetails.setYearsExperience(request.getYearsExperience());
        if (request.getHappyCustomersCount() != null) farmDetails.setHappyCustomersCount(request.getHappyCustomersCount());
        if (request.getFarmerName() != null) farmDetails.setFarmerName(request.getFarmerName());
        if (request.getMainCrops() != null) farmDetails.setMainCrops(request.getMainCrops());
        if (request.getIrrigationSource() != null) farmDetails.setIrrigationSource(request.getIrrigationSource());
        if (request.getSoilType() != null) farmDetails.setSoilType(request.getSoilType());
        if (request.getAboutFarm() != null) farmDetails.setAboutFarm(request.getAboutFarm());

        // File handling
        if (farmerPhoto != null && !farmerPhoto.isEmpty()) {
            String photoPath = farmFileStorageService.saveFarmerPhoto(farmerPhoto);
            farmDetails.setFarmerPhoto(photoPath);
        }

        if (farmVideo != null && !farmVideo.isEmpty()) {
            String videoPath = farmFileStorageService.saveFarmVideo(farmVideo);
            farmDetails.setFarmVideo(videoPath);
        }

        FarmDetails savedFarmDetails = farmDetailsRepository.save(farmDetails);
        return mapToResponse(savedFarmDetails);
    }

    @Override
    public FarmDetailsResponse getFarmDetailsByFarmerId(UUID farmerId) {
        Optional<FarmDetails> farmDetailsOpt = farmDetailsRepository.findByFarmerId(farmerId);
        if (farmDetailsOpt.isPresent()) {
            return mapToResponse(farmDetailsOpt.get());
        }
        return null; // Return null gracefully so the frontend receives 200 OK with data: null
    }

    @Override
    public void deleteFarmDetails(UUID farmerId) throws IOException {
        FarmDetails farmDetails = farmDetailsRepository.findByFarmerId(farmerId)
                .orElseThrow(() -> new RuntimeException("Farm Details not found for farmer id: " + farmerId));

        // Delete photo
        if (farmDetails.getFarmerPhoto() != null) {
            farmFileStorageService.deleteFile(farmDetails.getFarmerPhoto());
        }

        // Delete video
        if (farmDetails.getFarmVideo() != null) {
            farmFileStorageService.deleteFile(farmDetails.getFarmVideo());
        }

        // Delete DB record
        farmDetailsRepository.delete(farmDetails);
    }

    private FarmDetailsResponse mapToResponse(FarmDetails farmDetails) {
        FarmDetailsResponse response = new FarmDetailsResponse();
        response.setId(farmDetails.getId());
        response.setFarmerId(farmDetails.getFarmer().getId());
        response.setFarmName(farmDetails.getFarmName());
        response.setTagline(farmDetails.getTagline());
        response.setLocation(farmDetails.getLocation());
        response.setFarmingSince(farmDetails.getFarmingSince());
        response.setFarmingType(farmDetails.getFarmingType());
        response.setFarmArea(farmDetails.getFarmArea());
        response.setFarmAreaUnit(farmDetails.getFarmAreaUnit());
        response.setCropsGrownCount(farmDetails.getCropsGrownCount());
        response.setYearsExperience(farmDetails.getYearsExperience());
        response.setHappyCustomersCount(farmDetails.getHappyCustomersCount());
        response.setFarmerName(farmDetails.getFarmerName());
        response.setMainCrops(farmDetails.getMainCrops());
        response.setIrrigationSource(farmDetails.getIrrigationSource());
        response.setSoilType(farmDetails.getSoilType());
        response.setAboutFarm(farmDetails.getAboutFarm());
        response.setFarmerPhoto(farmDetails.getFarmerPhoto());
        response.setFarmVideo(farmDetails.getFarmVideo());
        response.setVerified(farmDetails.getVerified());
        response.setVerifiedOn(farmDetails.getVerifiedOn());
        response.setCertificateFile(farmDetails.getCertificateFile());
        return response;
    }
}
