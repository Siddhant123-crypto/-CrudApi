package com.Siddhant.UserApp.Service;

import com.Siddhant.UserApp.Entity.FarmerProfile;
import com.Siddhant.UserApp.Repository.FarmerProfileRepository;
import com.Siddhant.UserApp.dto.FarmerRequest;
import com.Siddhant.UserApp.dto.FarmerResponse;
import com.Siddhant.UserApp.dto.LoginRequest;
import com.Siddhant.UserApp.dto.LoginResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public abstract class FarmerService {

    @Autowired
    private FarmerProfileRepository farmerProfileRepository;

    public FarmerResponse saveFarmer(FarmerRequest request, MultipartFile photo) {

        // Name Validation
        System.out.println("Received Name: " + request.getName());
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("Name is required");
        }

        String farmerName = request.getName().trim();

        if (!farmerName.matches("^[A-Za-z]+(\\s+[A-Za-z]+)+$")) {
            throw new RuntimeException("Please enter first name and last name");
        }

        // Email Validation
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new RuntimeException("Please enter valid email");
        }

        if (farmerProfileRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Mobile Validation
        if (request.getMobile() == null || request.getMobile().trim().isEmpty()) {
            throw new RuntimeException("Mobile number is required");
        }

        if (!request.getMobile().matches("^[6-9]\\d{9}$")) {
            throw new RuntimeException("Please enter valid 10 digit mobile number");
        }

        if (farmerProfileRepository.findByMobile(request.getMobile()).isPresent()) {
            throw new RuntimeException("Mobile number already exists");
        }

        // Password Validation
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Password is required");
        }

        if (!request.getPassword().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{6,}$")) {
            throw new RuntimeException("Password must contain uppercase, lowercase, number and special character");
        }

        // State Validation
        if (request.getState() == null || request.getState().trim().isEmpty()) {
            throw new RuntimeException("State is required");
        }

        FarmerProfile farmer = new FarmerProfile();

        farmer.setName(request.getName());
        farmer.setVillage(request.getVillage());
        farmer.setAddress(request.getAddress());
        farmer.setPostalCode(request.getPostalCode());
        farmer.setMobile(request.getMobile());
        farmer.setEmail(request.getEmail());
        farmer.setState(request.getState());
        farmer.setPassword(request.getPassword());

        // Save Profile Photo
        System.out.println("Original Name : " + photo.getOriginalFilename());
        System.out.println("Content Type  : " + photo.getContentType());
        System.out.println("Size          : " + photo.getSize());
        System.out.println("Upload Path   : " + System.getProperty("user.dir"));
        if (photo != null && !photo.isEmpty()) {

            try {

                String fileName = System.currentTimeMillis() + "_" + photo.getOriginalFilename();

                String uploadDir = System.getProperty("user.dir") + "/uploads/";

                java.io.File folder = new java.io.File(uploadDir);

                if (!folder.exists()) {
                    folder.mkdirs();
                }

                java.io.File file = new java.io.File(uploadDir + fileName);

                java.nio.file.Files.copy(
                        photo.getInputStream(),
                        file.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );

                farmer.setProfilePhoto(fileName);

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            }
        }
        farmer.setCreatedBy(request.getName());
        farmer.setCreatedOn(LocalDateTime.now());

        farmer.setUpdatedBy(request.getName());
        farmer.setUpdatedOn(LocalDateTime.now());

        farmer.setIsActive(true);
        farmer.setIsDelete(false);
        farmer.setStatus("ACTIVE");

        FarmerProfile savedFarmer = farmerProfileRepository.save(farmer);

        FarmerResponse response = new FarmerResponse();

        response.setFarmerId(savedFarmer.getId());
        response.setName(savedFarmer.getName());
        response.setVillage(savedFarmer.getVillage());
        response.setAddress(savedFarmer.getAddress());
        response.setPostalCode(savedFarmer.getPostalCode());
        response.setMobile(savedFarmer.getMobile());
        response.setEmail(savedFarmer.getEmail());
        response.setState(savedFarmer.getState());
        response.setProfilePhoto(savedFarmer.getProfilePhoto());

        response.setCreatedBy(savedFarmer.getCreatedBy());
        response.setCreatedOn(savedFarmer.getCreatedOn());

        response.setUpdatedBy(savedFarmer.getUpdatedBy());
        response.setUpdatedOn(savedFarmer.getUpdatedOn());

        response.setIsActive(savedFarmer.getIsActive());
        response.setIsDelete(savedFarmer.getIsDelete());
        response.setStatus(savedFarmer.getStatus());

        return response;
    }

    public abstract FarmerResponse saveFarmer(FarmerRequest request);

    public List<FarmerResponse> getAllFarmers() {
        return List.of();
    }

    public abstract FarmerResponse getFarmerById(UUID id);

    public abstract FarmerResponse updateFarmer(UUID id, FarmerRequest request);

    public abstract String deleteFarmer(UUID id);

    // Farmer Login
    public abstract LoginResponse login(LoginRequest request);

    // Farmer Photo Upload
    public abstract String uploadPhoto(UUID farmerId, MultipartFile file);
}