package com.Siddhant.UserApp.Service.Impl;

import com.Siddhant.UserApp.Entity.FarmerProfile;
import com.Siddhant.UserApp.Repository.FarmerProfileRepository;
import com.Siddhant.UserApp.Service.FarmerService;
import com.Siddhant.UserApp.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.Siddhant.UserApp.Entity.Role;
import com.Siddhant.UserApp.Service.FileStorageService;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class FarmerServiceImpl extends FarmerService {


    @Autowired
    private FarmerProfileRepository farmerProfileRepository;

    @Autowired
    private FileStorageService fileStorageService;


    @Override
    public FarmerResponse saveFarmer(FarmerRequest request) {

        FarmerProfile farmer = new FarmerProfile();

        farmer.setName(request.getName());
        farmer.setVillage(request.getVillage());
        farmer.setAddress(request.getAddress());
        farmer.setPostalCode(request.getPostalCode());
        farmer.setMobile(request.getMobile());
        farmer.setEmail(request.getEmail());
        farmer.setState(request.getState());
        farmer.setPassword(request.getPassword());
        farmer.setRole(Role.FARMER);

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


    @Override
    public List<FarmerResponse> getAllFarmers() {

        List<FarmerProfile> farmers = farmerProfileRepository.findAll();

        return farmers.stream()
                .map(farmer -> {

                    FarmerResponse response = new FarmerResponse();

                    response.setFarmerId(farmer.getId());
                    response.setName(farmer.getName());
                    response.setVillage(farmer.getVillage());
                    response.setAddress(farmer.getAddress());
                    response.setPostalCode(farmer.getPostalCode());
                    response.setMobile(farmer.getMobile());
                    response.setEmail(farmer.getEmail());
                    response.setState(farmer.getState());
                    response.setProfilePhoto(farmer.getProfilePhoto());

                    response.setCreatedBy(farmer.getCreatedBy());
                    response.setCreatedOn(farmer.getCreatedOn());

                    response.setUpdatedBy(farmer.getUpdatedBy());
                    response.setUpdatedOn(farmer.getUpdatedOn());

                    response.setIsActive(farmer.getIsActive());
                    response.setIsDelete(farmer.getIsDelete());

                    response.setStatus(farmer.getStatus());

                    return response;

                })
                .toList();
    }



        @Override
        public FarmerResponse getFarmerById(UUID id) {

            FarmerProfile farmer = farmerProfileRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Farmer not found"));

            FarmerResponse response = new FarmerResponse();

            response.setFarmerId(farmer.getId());
            response.setName(farmer.getName());
            response.setVillage(farmer.getVillage());
            response.setAddress(farmer.getAddress());
            response.setPostalCode(farmer.getPostalCode());
            response.setMobile(farmer.getMobile());
            response.setEmail(farmer.getEmail());
            response.setState(farmer.getState());
            response.setProfilePhoto(farmer.getProfilePhoto());

            response.setCreatedBy(farmer.getCreatedBy());
            response.setCreatedOn(farmer.getCreatedOn());

            response.setUpdatedBy(farmer.getUpdatedBy());
            response.setUpdatedOn(farmer.getUpdatedOn());

            response.setIsActive(farmer.getIsActive());
            response.setIsDelete(farmer.getIsDelete());
            response.setStatus(farmer.getStatus());

            return response;
        }

    @Override
    public FarmerResponse updateFarmer(UUID id, FarmerRequest request) {

        FarmerProfile farmer = farmerProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        farmer.setName(request.getName());
        farmer.setVillage(request.getVillage());
        farmer.setAddress(request.getAddress());
        farmer.setPostalCode(request.getPostalCode());
        farmer.setMobile(request.getMobile());
        farmer.setEmail(request.getEmail());
        farmer.setState(request.getState());
        farmer.setPassword(request.getPassword());


        farmer.setUpdatedBy(request.getName());
        farmer.setUpdatedOn(LocalDateTime.now());

        farmerProfileRepository.save(farmer);

        FarmerResponse response = new FarmerResponse();
        response.setFarmerId(farmer.getId());
        response.setName(farmer.getName());
        response.setVillage(farmer.getVillage());
        response.setAddress(farmer.getAddress());
        response.setPostalCode(farmer.getPostalCode());
        response.setMobile(farmer.getMobile());
        response.setEmail(farmer.getEmail());
        response.setState(farmer.getState());
        response.setProfilePhoto(farmer.getProfilePhoto());
        response.setCreatedBy(farmer.getCreatedBy());
        response.setCreatedOn(farmer.getCreatedOn());
        response.setUpdatedBy(farmer.getUpdatedBy());
        response.setUpdatedOn(farmer.getUpdatedOn());
        response.setIsActive(farmer.getIsActive());
        response.setIsDelete(farmer.getIsDelete());
        response.setStatus(farmer.getStatus());

        return response;
    }
    @Override
    public String deleteFarmer(UUID id) {

        FarmerProfile farmer = farmerProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        farmer.setIsDelete(true);
        farmer.setIsActive(false);
        farmer.setStatus("DELETED");
        farmer.setUpdatedOn(LocalDateTime.now());

        farmerProfileRepository.save(farmer);

        return "Farmer deleted successfully";
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        try {

            Optional<FarmerProfile> optional =
                    farmerProfileRepository.findByEmail(request.getEmail());

            if (optional.isEmpty()) {
                return new LoginResponse("Email not found", null);
            }

            FarmerProfile farmer = optional.get();

            if (!farmer.getPassword().equals(request.getPassword())) {
                return new LoginResponse("Incorrect Password", null);
            }

            if (!farmer.getIsActive()) {
                return new LoginResponse("Farmer Account is Inactive", null);
            }

            if (farmer.getIsDelete()) {
                return new LoginResponse("Farmer Account Deleted", null);
            }

            // Login Count
            if (farmer.getLoginCount() == null) {
                farmer.setLoginCount(1);
            } else {
                farmer.setLoginCount(farmer.getLoginCount() + 1);
            }

            // Last Login
            farmer.setLastLogin(LocalDateTime.now());

            farmerProfileRepository.save(farmer);

            LoginData response = new LoginData();
            response.setFarmerId(farmer.getId());
            response.setName(farmer.getName());
            response.setEmail(farmer.getEmail());
            response.setRole(Role.FARMER);
            response.setProfilePhoto(farmer.getProfilePhoto());
            System.out.println("Photo = " + response.getProfilePhoto());


            return new LoginResponse(
                    "Login Successful. Login Count : " + farmer.getLoginCount(),
                    response
            );

        } catch (Exception e) {
            e.printStackTrace();
            return new LoginResponse(
                    e.getMessage(),
                    null
            );

        }
    }
    @Override
    public String uploadPhoto(UUID farmerId, MultipartFile file) {

        try {

            System.out.println("Farmer ID : " + farmerId);

            Optional<FarmerProfile> optional = farmerProfileRepository.findById(farmerId);

            if (optional.isEmpty()) {
                System.out.println("Farmer Not Found");
                return "Farmer Not Found";
            }

            FarmerProfile farmer = optional.get();

            System.out.println("File Empty : " + file.isEmpty());
            System.out.println("Original Name : " + file.getOriginalFilename());
            System.out.println("Size : " + file.getSize());

            System.out.println("Farmer Name : " + farmer.getName());

            String fileName = fileStorageService.saveFile(file);

            System.out.println("Saved File : " + fileName);

            farmer.setProfilePhoto(fileName);

            FarmerProfile savedFarmer = farmerProfileRepository.save(farmer);

            System.out.println("DB Value : " + savedFarmer.getProfilePhoto());

            return "Photo Uploaded Successfully";

        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}

