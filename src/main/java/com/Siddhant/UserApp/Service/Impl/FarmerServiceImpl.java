package com.Siddhant.UserApp.Service.Impl;

import com.Siddhant.UserApp.Entity.FarmerProfile;
import com.Siddhant.UserApp.Entity.Role;
import com.Siddhant.UserApp.Entity.Status;
import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.Repository.FarmerProfileRepository;
import com.Siddhant.UserApp.Repository.UserRepository;
import com.Siddhant.UserApp.Service.FarmerService;
import com.Siddhant.UserApp.Service.FileStorageService;
import com.Siddhant.UserApp.Service.JwtService;
import com.Siddhant.UserApp.dto.*;
import com.Siddhant.UserApp.mapper.MapperBuild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FarmerServiceImpl implements FarmerService {

    private static final Logger log = LoggerFactory.getLogger(FarmerServiceImpl.class);

    private final FarmerProfileRepository farmerProfileRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final JwtService jwtService;

    @Autowired
    public FarmerServiceImpl(FarmerProfileRepository farmerProfileRepository, 
                             UserRepository userRepository,
                             FileStorageService fileStorageService, 
                             JwtService jwtService) {
        this.farmerProfileRepository = farmerProfileRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.jwtService = jwtService;
    }

    @Override
    public FarmerResponse saveFarmer(FarmerRequest request, MultipartFile photo) {
        try {
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new RuntimeException("Email already exists");
            }
            if (userRepository.findByMobile(request.getMobile()).isPresent()) {
                throw new RuntimeException("Mobile already exists");
            }

            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setMobile(request.getMobile());
            user.setPassword(request.getPassword());
            user.setState(request.getState());
            user.setVillage(request.getVillage());
            user.setAddress(request.getAddress());
            user.setPostalCode(request.getPostalCode());
            user.setRole(Role.FARMER);
            user.setIsActive(true);
            user.setIsDelete(false);
            user.setStatus(Status.ACTIVE);
            
            LocalDateTime now = LocalDateTime.now();
            user.setCreatedOn(now);
            user.setUpdatedOn(now);
            user.setCreatedBy(request.getName());
            user.setUpdatedBy(request.getName());

            if (photo != null && !photo.isEmpty()) {
                log.debug("Processing profile photo: {}, Size: {}", photo.getOriginalFilename(), photo.getSize());
                String fileName = fileStorageService.saveFile(photo);
                user.setProfilePhoto(fileName);
                log.debug("Profile Photo Saved: {}", fileName);
            }

            user = userRepository.save(user);

            FarmerProfile farmer = new FarmerProfile();
            farmer.setUser(user);
            // If farmName is in request, set it here. We can leave it null or set it if added to FarmerRequest.
            
            FarmerProfile savedFarmer = farmerProfileRepository.save(farmer);
            
            String token = jwtService.generateToken(user.getEmail(), Role.FARMER.name());
            FarmerResponse response = MapperBuild.buildFarmerResponse(savedFarmer);
            response.setAccessToken(token);
            response.setExpiresIn(86400L);
            log.info("Farmer Registration Successful for email: {}", user.getEmail());
            
            return response;
        } catch (Exception e) {
            log.error("Error saving farmer", e);
            throw new RuntimeException("Error saving farmer: " + e.getMessage());
        }
    }

    @Override
    public List<FarmerResponse> getAllFarmers() {
        List<FarmerProfile> farmers = farmerProfileRepository.findAll();
        return farmers.stream()
                .map(MapperBuild::buildFarmerResponse)
                .toList();
    }

    @Override
    public FarmerResponse getFarmerById(UUID id) {
        FarmerProfile farmer = farmerProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));
        return MapperBuild.buildFarmerResponse(farmer);
    }

    @Override
    public FarmerResponse updateFarmer(UUID id, FarmerRequest request) {
        try {
            FarmerProfile farmer = farmerProfileRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Farmer not found"));

            User user = farmer.getUser();
            user.setName(request.getName());
            user.setVillage(request.getVillage());
            user.setAddress(request.getAddress());
            user.setPostalCode(request.getPostalCode());
            user.setMobile(request.getMobile());
            user.setEmail(request.getEmail());
            user.setState(request.getState());
            user.setPassword(request.getPassword());
            user.setUpdatedBy(request.getName());
            user.setUpdatedOn(LocalDateTime.now());
            
            userRepository.save(user);
            
            return MapperBuild.buildFarmerResponse(farmer);
        } catch (Exception e) {
            log.error("Error updating farmer", e);
            throw new RuntimeException("Error updating farmer: " + e.getMessage());
        }
    }

    @Override
    public String deleteFarmer(UUID id) {
        try {
            FarmerProfile farmer = farmerProfileRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Farmer not found"));
            
            User user = farmer.getUser();
            user.setIsDelete(true);
            user.setIsActive(false);
            user.setStatus(Status.INACTIVE);
            user.setUpdatedOn(LocalDateTime.now());
            
            userRepository.save(user);
            return "Farmer deleted successfully";
        } catch (Exception e) {
            log.error("Error deleting farmer", e);
            return "Error deleting farmer: " + e.getMessage();
        }
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            Optional<User> optional = request.getEmail().contains("@")
                    ? userRepository.findByEmail(request.getEmail())
                    : userRepository.findByMobile(request.getEmail());

            if (optional.isEmpty()) {
                return new LoginResponse("Email not found", null, null);
            }

            User user = optional.get();

            if (user.getRole() != Role.FARMER) {
                return new LoginResponse("Not a Farmer account", null, null);
            }

            if (!user.getPassword().equals(request.getPassword())) {
                return new LoginResponse("Incorrect Password", null, null);
            }
            if (!user.getIsActive()) {
                return new LoginResponse("Farmer Account is Inactive", null, null);
            }
            if (user.getIsDelete()) {
                return new LoginResponse("Farmer Account Deleted", null, null);
            }
            
            user.setLoginCount(user.getLoginCount() == null ? 1 : user.getLoginCount() + 1);
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
            
            FarmerProfile farmer = farmerProfileRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Farmer profile missing for user"));

            LoginData data = new LoginData();
            data.setFarmerId(farmer.getId());
            data.setName(user.getName());
            data.setEmail(user.getEmail());
            data.setRole(Role.FARMER);
            data.setProfilePhoto(user.getProfilePhoto());
            
            log.debug("Farmer logged in: {}", user.getEmail());
            LoginResponse tokenResponse = new LoginResponse();
            String token = jwtService.generateToken(user.getEmail(), Role.FARMER.name());
            tokenResponse.setAccessToken(token);
            tokenResponse.setExpiresIn(86400); // 24 hours
            tokenResponse.setMessage("Login Successful. Login Count : " + user.getLoginCount());
            tokenResponse.setResponse(data);
            return tokenResponse;
        } catch (Exception e) {
            log.error("Farmer login failed", e);
            return new LoginResponse("Login Failed : " + e.getMessage(), null, null);
        }
    }

    @Override
    public String uploadPhoto(UUID farmerId, MultipartFile file) {
        try {
            log.info("Uploading photo for Farmer ID: {}", farmerId);
            Optional<FarmerProfile> optional = farmerProfileRepository.findById(farmerId);
            if (optional.isEmpty()) {
                log.warn("Farmer Not Found for ID: {}", farmerId);
                return "Farmer Not Found";
            }
            FarmerProfile farmer = optional.get();
            User user = farmer.getUser();
            
            log.debug("Original Name: {}, Size: {}", file.getOriginalFilename(), file.getSize());
            String fileName = fileStorageService.saveFile(file);
            log.debug("Saved File Name: {}", fileName);
            
            user.setProfilePhoto(fileName);
            userRepository.save(user);
            
            return "Photo Uploaded Successfully";
        } catch (Exception e) {
            log.error("Error uploading farmer photo", e);
            return e.getMessage();
        }
    }

    @Override
    public List<FarmerResponse> getFarmersByState(String state) {
        List<FarmerProfile> farmers = farmerProfileRepository.findByUser_StateIgnoreCase(state);
        return farmers.stream().map(MapperBuild::buildFarmerResponse).toList();
    }

    @Override
    public List<FarmerResponse> getNearbyFarmers(String state, String village) {
        List<FarmerProfile> farmers = farmerProfileRepository.findByUser_StateAndUser_Village(state, village);
        return farmers.stream().map(MapperBuild::buildFarmerResponse).toList();
    }
}
