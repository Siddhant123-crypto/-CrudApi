package com.Siddhant.UserApp.Controller;

import com.Siddhant.UserApp.Service.UserMappingService;
import com.Siddhant.UserApp.dto.UserMappingRequest;
import com.Siddhant.UserApp.dto.UserMappingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/userMapping")
public class UserMappingController {

    @Autowired
    private UserMappingService userMappingService;

    @PostMapping("/save")
    public UserMappingResponse saveUserMapping(@RequestBody UserMappingRequest request) {

        System.out.println("User ID : " + request.getUserId());
        System.out.println("Name : " + request.getName());
        System.out.println("Address : " + request.getAddress());
        System.out.println("Pincode : " + request.getPincode());
        System.out.println("Mobile No : " + request.getMobileNo());
        System.out.println("City : " + request.getCity());
        System.out.println("State : " + request.getState());

        return userMappingService.saveUserMapping(request);
    }

    @PutMapping("/update/{id}")
    public UserMappingResponse updateUserMapping(
            @PathVariable UUID id,
            @RequestBody UserMappingRequest request) {

        return userMappingService.updateUserMapping(id, request);
    }
}
