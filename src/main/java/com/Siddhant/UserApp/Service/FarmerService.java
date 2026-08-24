package com.Siddhant.UserApp.Service;

import com.Siddhant.UserApp.dto.FarmerRequest;
import com.Siddhant.UserApp.dto.FarmerResponse;
import com.Siddhant.UserApp.dto.LoginRequest;
import com.Siddhant.UserApp.dto.LoginResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface FarmerService {
    FarmerResponse saveFarmer(FarmerRequest request, MultipartFile photo);
    List<FarmerResponse> getAllFarmers();
    FarmerResponse getFarmerById(UUID id);
    FarmerResponse updateFarmer(UUID id, FarmerRequest request);
    String deleteFarmer(UUID id);
    LoginResponse login(LoginRequest request);
    String uploadPhoto(UUID farmerId, MultipartFile file);
    List<FarmerResponse> getFarmersByState(String state);
    List<FarmerResponse> getNearbyFarmers(String state, String village);
}