package com.Siddhant.UserApp.Controller;
import java.util.List;
import java.util.UUID;

import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.Service.FarmerService;
import com.Siddhant.UserApp.dto.FarmerRequest;
import com.Siddhant.UserApp.dto.FarmerResponse;
import com.Siddhant.UserApp.dto.LoginRequest;
import com.Siddhant.UserApp.dto.LoginResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestPart;

@RestController
@RequestMapping("/farmer")
public class FarmerController {

    @Autowired
    private FarmerService farmerService;


    @PostMapping(
            value = "/save",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public FarmerResponse saveFarmer(
            @RequestParam("data") String data,
            @RequestPart(value = "photo", required = false) MultipartFile photo
    ) throws java.io.IOException {

        System.out.println("Farmer Save API Called");

        FarmerRequest request = new ObjectMapper().readValue(data, FarmerRequest.class);

        return farmerService.saveFarmer(request, photo);
    }
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return farmerService.login(request);
    }

    // Get All Farmers
    @GetMapping("/getAll")
    public List<FarmerResponse> getAllFarmers() {

        return farmerService.getAllFarmers();
    }


    // Get Farmer By IDAC
    @GetMapping("/getById/{id}")
    public FarmerResponse getFarmerById(@PathVariable UUID id) {

        return farmerService.getFarmerById(id);
    }


    // Update Farmer By ID
    @PutMapping("/update/{id}")
    public ResponseEntity<FarmerResponse> updateFarmer(
            @PathVariable UUID id,
            @RequestBody FarmerRequest request) {

        FarmerResponse response = farmerService.updateFarmer(id, request);

        return ResponseEntity.ok(response);
    }


    // Delete Farmer By ID
    @DeleteMapping("/delete/{id}")
    public String deleteFarmer(@PathVariable UUID id) {

        return farmerService.deleteFarmer(id);
    }

    @PostMapping("/uploadPhoto/{id}")
    public String uploadPhoto(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {

        return farmerService.uploadPhoto(id, file);
    }
    @GetMapping("/getPhoto/{id}")
    public String getPhoto(@PathVariable UUID id) {

        FarmerResponse farmer = farmerService.getFarmerById(id);

        return farmer.getProfilePhoto();
    }
    @GetMapping("/nearby")
    public List<FarmerResponse> getNearbyFarmers(
            @RequestParam String state,
            @RequestParam String village) {

        return farmerService.getNearbyFarmers(state, village);
    }
    @GetMapping("/state/{state}")
    public ResponseEntity<List<FarmerResponse>> getFarmersByState(@PathVariable String state) {

        List<FarmerResponse> farmers = farmerService.getFarmersByState(state);

        return ResponseEntity.ok(farmers);
    }
}