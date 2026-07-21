package com.Siddhant.UserApp.Service.Impl;

import com.Siddhant.UserApp.Repository.FarmerProfileRepository;
import com.Siddhant.UserApp.Repository.UserRepository;
import com.Siddhant.UserApp.Service.AuthService;
import com.Siddhant.UserApp.dto.LoginRequest;
import com.Siddhant.UserApp.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Siddhant.UserApp.Entity.FarmerProfile;
import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.dto.LoginData;
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private FarmerProfileRepository farmerProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public LoginResponse login(LoginRequest request) {

        // Farmer Check
        var farmerOptional = farmerProfileRepository.findByEmail(request.getEmail());
        System.out.println("Login Email : " + request.getEmail());
        System.out.println("Farmer Found : " + farmerOptional.isPresent());

        if (farmerOptional.isPresent()) {

            FarmerProfile farmer = farmerOptional.get();
            System.out.println("Email : " + farmer.getEmail());
            System.out.println("Role  : " + farmer.getRole());
            System.out.println("Id    : " + farmer.getId());
            System.out.println("User  : " + farmer.getUser());

            if (!farmer.getPassword().equals(request.getPassword())) {
                return new LoginResponse("Incorrect Password", null);
            }

            if (!farmer.getIsActive()) {
                return new LoginResponse("Farmer Account Inactive", null);
            }

            if (farmer.getIsDelete()) {
                return new LoginResponse("Farmer Account Deleted", null);
            }

            LoginData data = new LoginData();
            data.setName(farmer.getName());
            data.setEmail(farmer.getEmail());
            data.setRole(farmer.getRole());
            data.setFarmerId(farmer.getId());

            return new LoginResponse("Login Successful", data);
        }


        // User (Customer) Check
        var userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isPresent()) {

            User user = userOptional.get();

            System.out.println("========== USER ==========");
            System.out.println("User Id : " + user.getUserId());
            System.out.println("Email   : " + user.getEmail());
            System.out.println("Role    : " + user.getRole());
            System.out.println("Password: " + user.getPassword());

            if (!user.getPassword().equals(request.getPassword())) {
                return new LoginResponse("Incorrect Password", null);
            }

            if (!user.getIsActive()) {
                return new LoginResponse("User Account Inactive", null);
            }

            if (user.getIsDelete()) {
                return new LoginResponse("User Account Deleted", null);
            }

            LoginData data = new LoginData();
            data.setName(user.getName());
            data.setEmail(user.getEmail());
            data.setRole(user.getRole());
            data.setUserId(user.getUserId());

            return new LoginResponse("Login Successful", data);
        }

        return new LoginResponse("Email not found", null);
    }
}
