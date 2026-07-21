package com.Siddhant.UserApp.Service.Impl;

import com.Siddhant.UserApp.Entity.FarmerProfile;
import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.Repository.FarmerProfileRepository;
import com.Siddhant.UserApp.Repository.UserRepository;
import com.Siddhant.UserApp.Service.NewUserService;
import com.Siddhant.UserApp.dto.NewUserData;
import com.Siddhant.UserApp.dto.NewUserLoginRequest;
import com.Siddhant.UserApp.dto.NewUserLoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class NewUserServiceImpl implements NewUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FarmerProfileRepository farmerProfileRepository;

    @Override
    public NewUserLoginResponse login(NewUserLoginRequest request) {

        try {

            // ================= CUSTOMER =================

            Optional<User> userOptional =
                    userRepository.findByEmail(request.getEmail());

            if (userOptional.isPresent()) {

                User user = userOptional.get();

                if (!user.getPassword().equals(request.getPassword())) {
                    return new NewUserLoginResponse("Incorrect Password", null);
                }

                if (!user.getIsActive()) {
                    return new NewUserLoginResponse("Customer Account Inactive", null);
                }

                if (user.getIsDelete()) {
                    return new NewUserLoginResponse("Customer Account Deleted", null);
                }

                if (user.getLoginCount() == null) {
                    user.setLoginCount(1);
                } else {
                    user.setLoginCount(user.getLoginCount() + 1);
                }

                user.setLastLogin(LocalDateTime.now());

                userRepository.save(user);

                NewUserData data = new NewUserData();
                data.setEmail(user.getEmail());
                data.setRole("Customer");

                return new NewUserLoginResponse(
                        "Login Successful",
                        data
                );
            }

            // ================= FARMER =================

            Optional<FarmerProfile> farmerOptional =
                    farmerProfileRepository.findByEmail(request.getEmail());

            if (farmerOptional.isPresent()) {

                FarmerProfile farmer = farmerOptional.get();

                if (!farmer.getPassword().equals(request.getPassword())) {
                    return new NewUserLoginResponse("Incorrect Password", null);
                }

                if (!farmer.getIsActive()) {
                    return new NewUserLoginResponse("Farmer Account Inactive", null);
                }

                if (farmer.getIsDelete()) {
                    return new NewUserLoginResponse("Farmer Account Deleted", null);
                }

                if (farmer.getLoginCount() == null) {
                    farmer.setLoginCount(1);
                } else {
                    farmer.setLoginCount(farmer.getLoginCount() + 1);
                }

                farmer.setLastLogin(LocalDateTime.now());

                farmerProfileRepository.save(farmer);

                NewUserData data = new NewUserData();
                data.setEmail(farmer.getEmail());
                data.setRole("Farmer");

                return new NewUserLoginResponse(
                        "Login Successful",
                        data
                );
            }

            return new NewUserLoginResponse("Email Not Found", null);

        } catch (Exception e) {

            return new NewUserLoginResponse(
                    e.getMessage(),
                    null
            );

        }

    }
}