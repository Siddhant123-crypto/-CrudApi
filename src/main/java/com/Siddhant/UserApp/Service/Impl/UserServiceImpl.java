package com.Siddhant.UserApp.Service.Impl;

import com.Siddhant.UserApp.Entity.Role;
import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.Entity.Status;
import com.Siddhant.UserApp.Repository.UserRepository;
import com.Siddhant.UserApp.Service.FileStorageService;
import com.Siddhant.UserApp.Service.KeycloakService;
import com.Siddhant.UserApp.Service.UserService;
import com.Siddhant.UserApp.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KeycloakService keycloakService;

    @Override
    public RegisterResponse register(RegisterData request, MultipartFile photo) {

        try {

            // Name Validation
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                return new RegisterResponse("Name is required", null);
            }

            if (!request.getEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                return new RegisterResponse("Please enter a valid email", null);
            }

            // Email Validation
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return new RegisterResponse("email is required",null);
            }

            System.out.println("Received Name = [" + request.getName() + "]");

//            if (!request.getName().matches("^[A-Za-z]+(\\s+[A-Za-z]+)+$")) {
//                return new RegisterResponse("Please enter a valid full name", null);
//            }

            // Mobile Validation
            if (request.getMobile() == null || request.getMobile().trim().isEmpty()) {
                return new RegisterResponse("Mobile number is required",null);
            }

            if (!request.getMobile().matches("^[6-9]\\d{9}$")) {
                return new RegisterResponse("Please enter 10 digit mobile number",null);
            }

            // Password Validation
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return new RegisterResponse("Password is required",null);
            }

            if (!request.getPassword().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{6,}$")) {
                return new RegisterResponse("Password must contain uppercase, lowercase, number and special character",null);
            }

            // State Validation
            if (request.getState() == null || request.getState().trim().isEmpty()) {
                return new RegisterResponse( "State is required",null);
            }

            // Email Duplicate
            Optional<User> email = userRepository.findByEmail(request.getEmail());

            if (email.isPresent()) {
                return new RegisterResponse("Email already exists",null);
            }

            // Mobile Duplicate
            Optional<User> mobile = userRepository.findByMobile(request.getMobile());

            if (mobile.isPresent()) {
                return new RegisterResponse("Mobile number already exists",null);
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
            
            if (photo != null && !photo.isEmpty()) {
                String fileName = fileStorageService.saveFile(photo);
                user.setProfilePhoto(fileName);
            } else {
                user.setProfilePhoto(request.getProfilePhoto());
            }

            user.setIsActive(true);
            user.setIsDelete(false);
            user.setStatus(Status.ACTIVE);
            user.setRole(Role.USER);

            user.setCreatedOn(LocalDateTime.now());

            user.setCreatedOn(LocalDateTime.now());
            user.setUpdatedOn(LocalDateTime.now());

            user.setCreatedBy("Admin");
            user.setUpdatedBy("Admin");

            String[] names = request.getName().trim().split("\\s+", 2);

            String firstName = names[0];
            String lastName = names.length > 1 ? names[1] : "";

            keycloakService.createUser(
                    firstName,
                    lastName,
                    request.getEmail(),
                    request.getPassword()
            );

            userRepository.save(user);

            RegisterData response = new RegisterData();

            response.setUserId(user.getUserId());
            response.setName(user.getName());
            response.setEmail(user.getEmail());
            response.setMobile(user.getMobile());
            response.setState(user.getState());
            response.setVillage(user.getVillage());
            response.setAddress(user.getAddress());
            response.setPostalCode(user.getPostalCode());

            response.setCreatedBy(user.getCreatedBy());
            response.setCreatedOn(user.getCreatedOn());

            response.setUpdatedBy(user.getUpdatedBy());
            response.setUpdatedOn(user.getUpdatedOn());

            response.setIsActive(user.getIsActive());
            response.setIsDelete(user.getIsDelete());

            response.setStatus(user.getStatus());

            return new RegisterResponse(
                    "Registration Successful",
                    response
            );

        } catch (Exception e) {

            return new RegisterResponse(
                    e.getMessage(),
                    null
            );

        }

    }
    @Override
    public LoginResponse login(LoginRequest request) {

        try {

            Optional<User> optional = request.getEmail().contains("@")
                    ? userRepository.findByEmail(request.getEmail())
                    : userRepository.findByMobile(request.getEmail());

            if (optional.isEmpty()) {
                return new LoginResponse("Email not found", null,null);
            }

            User user = optional.get();

            if (!user.getPassword().equals(request.getPassword())) {
                return new LoginResponse("Incorrect Password", null,null);
            }

            if (!user.getIsActive()) {
                return new LoginResponse("User Account is Inactive", null,null);
            }

            if (user.getIsDelete()) {
                return new LoginResponse("User Account Deleted", null,null);
            }

            user.setLastLogin(LocalDateTime.now());

            if (user.getLoginCount() == null) {
                user.setLoginCount(1);
            } else {
                user.setLoginCount(user.getLoginCount() + 1);
            }

            userRepository.save(user);

            LoginData response = new LoginData();

            response.setUserId(user.getUserId());
            response.setName(user.getName());
            response.setEmail(user.getEmail());
            response.setProfilePhoto(user.getProfilePhoto());
            response.setRole(user.getRole());
            LoginResponse tokenResponse = null;

            try {

                tokenResponse = keycloakService.getAccessToken(
                        user.getEmail(),
                        request.getPassword()
                );
            } catch (Exception e) {
                System.out.println("Keycloak auth failed, attempting to sync user: " + e.getMessage());
                try {
                    String[] names = user.getName().trim().split("\\s+", 2);
                    String firstName = names[0];
                    String lastName = names.length > 1 ? names[1] : "";
                    keycloakService.createUser(firstName, lastName, user.getEmail(), request.getPassword());
                    tokenResponse = keycloakService.getAccessToken(
                            user.getEmail(),
                            request.getPassword()
                    );
                } catch (Exception syncEx) {
                    return new LoginResponse("Invalid User Credentials or Keycloak Sync Failed", null, null);
                }
            }

            tokenResponse.setMessage(
                    "Login Successful. Login Count : " + user.getLoginCount()
            );

            tokenResponse.setResponse(response);

            return tokenResponse;
        } catch (Exception e) {

            return new LoginResponse("Login Failed: " + e.getMessage(), null,null);

        }
    }
    @Override
    public List<User> getAllUsers() {

        return userRepository.findAll();

    }

    @Override
    public User getUserById(UUID id) {

        Optional<User> optional = userRepository.findById(id);

        if (optional.isPresent()) {
            return optional.get();
        }

        return null;
    }
    @Override
    public UpdateResponse updateUser(UUID id, RegisterData request) {

        Optional<User> optional = userRepository.findById(id);

        if (optional.isEmpty()) {
            return new UpdateResponse("User Not Found", null);
        }

        User user = optional.get();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());
        user.setPassword(request.getPassword());
        user.setState(request.getState());
        user.setVillage(request.getVillage());
        user.setAddress(request.getAddress());
        user.setPostalCode(request.getPostalCode());


        user.setUpdatedOn(LocalDateTime.now());
        user.setUpdatedBy("Admin");

        userRepository.save(user);

        RegisterData response = new RegisterData();

        response.setUserId(user.getUserId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setMobile(user.getMobile());
        response.setPassword(user.getPassword());
        response.setState(user.getState());
        response.setVillage(user.getVillage());
        response.setAddress(user.getAddress());
        response.setPostalCode(user.getPostalCode());
        response.setProfilePhoto(user.getProfilePhoto());

        response.setCreatedBy(user.getCreatedBy());
        response.setCreatedOn(user.getCreatedOn());
        response.setUpdatedBy(user.getUpdatedBy());
        response.setUpdatedOn(user.getUpdatedOn());

        response.setIsActive(user.getIsActive());
        response.setIsDelete(user.getIsDelete());
        response.setStatus(user.getStatus());

        return new UpdateResponse(
                "User Updated Successfully",
                response
        );
    }

    @Override
    public String deleteUser(UUID id) {

        Optional<User> optional = userRepository.findById(id);

        if (optional.isEmpty()) {
            return "User Not Found";
        }

        User user = optional.get();

        user.setIsDelete(true);
        user.setIsActive(false);
        user.setLoginCount(0);
        user.setUpdatedOn(LocalDateTime.now());

        userRepository.save(user);

        return "User Deleted Successfully";
    }

    @Override
    public String uploadPhoto(UUID userId, MultipartFile file) {

        try {

            System.out.println("========== PHOTO UPLOAD ==========");
            System.out.println("User ID = " + userId);
            System.out.println("Original File = " + file.getOriginalFilename());
            System.out.println("Is Empty = " + file.isEmpty());

            Optional<User> optional = userRepository.findById(userId);

            if (optional.isEmpty()) {
                return "User Not Found";
            }

            User user = optional.get();

            String fileName = fileStorageService.saveFile(file);

            System.out.println("Saved File Name = " + fileName);

            user.setProfilePhoto(fileName);

            userRepository.save(user);

            System.out.println("Saved In DB = " + user.getProfilePhoto());

            return "Photo Uploaded Successfully";

        } catch (Exception e) {

            e.printStackTrace();

            return e.getMessage();
        }
    }

}
