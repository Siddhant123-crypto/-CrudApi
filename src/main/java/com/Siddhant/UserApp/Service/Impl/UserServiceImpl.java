package com.Siddhant.UserApp.Service.Impl;

import com.Siddhant.UserApp.Entity.CustomerProfile;
import com.Siddhant.UserApp.Entity.Role;
import com.Siddhant.UserApp.Entity.Status;
import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.Repository.CustomerRepository;
import com.Siddhant.UserApp.Repository.UserRepository;
import com.Siddhant.UserApp.Service.FileStorageService;
import com.Siddhant.UserApp.Service.JwtService;
import com.Siddhant.UserApp.Service.UserService;
import com.Siddhant.UserApp.dto.*;
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
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final JwtService jwtService;

    @Autowired
    public UserServiceImpl(FileStorageService fileStorageService, 
                           UserRepository userRepository, 
                           CustomerRepository customerRepository,
                           JwtService jwtService) {
        this.fileStorageService = fileStorageService;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.jwtService = jwtService;
    }

    @Override
    public RegisterResponse register(RegisterData request, MultipartFile photo) {
        try {
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                return new RegisterResponse("Name is required", null);
            }
            if (request.getMobile() == null || request.getMobile().trim().isEmpty()) {
                return new RegisterResponse("Mobile number is required", null);
            }
            if (!request.getMobile().matches("^[6-9]\\d{9}$")) {
                return new RegisterResponse("Please enter 10 digit mobile number", null);
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return new RegisterResponse("Password is required", null);
            }
            if (!request.getPassword().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{6,}$")) {
                return new RegisterResponse("Password must contain uppercase, lowercase, number and special character", null);
            }
            if (request.getState() == null || request.getState().trim().isEmpty()) {
                return new RegisterResponse("State is required", null);
            }
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                return new RegisterResponse("Email already exists", null);
            }
            if (userRepository.findByMobile(request.getMobile()).isPresent()) {
                return new RegisterResponse("Mobile number already exists", null);
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
            user.setRole(Role.CUSTOMER);
            
            LocalDateTime now = LocalDateTime.now();
            user.setCreatedOn(now);
            user.setUpdatedOn(now);
            user.setCreatedBy("Admin");
            user.setUpdatedBy("Admin");
            
            userRepository.save(user);

            CustomerProfile customerProfile = new CustomerProfile(user);
            customerRepository.save(customerProfile);

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
            response.setProfilePhoto(user.getProfilePhoto());
            
            String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
            
            return new RegisterResponse("Registration Successful", response, token, 604800);
        } catch (Exception e) {
            log.error("Registration Failed", e);
            return new RegisterResponse("Registration Failed: " + e.getMessage(), null);
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
            if (!user.getPassword().equals(request.getPassword())) {
                return new LoginResponse("Incorrect Password", null, null);
            }
            if (!user.getIsActive()) {
                return new LoginResponse("User Account is Inactive", null, null);
            }
            if (user.getIsDelete()) {
                return new LoginResponse("User Account Deleted", null, null);
            }
            
            user.setLastLogin(LocalDateTime.now());
            user.setLoginCount(user.getLoginCount() == null ? 1 : user.getLoginCount() + 1);
            userRepository.save(user);
            
            LoginData response = new LoginData();
            response.setUserId(user.getUserId());
            response.setName(user.getName());
            response.setEmail(user.getEmail());
            response.setProfilePhoto(user.getProfilePhoto());
            response.setRole(user.getRole());
            
            String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
            LoginResponse tokenResponse = new LoginResponse();
            tokenResponse.setAccessToken(token);
            tokenResponse.setExpiresIn(604800);
            tokenResponse.setMessage("Login Successful. Login Count : " + user.getLoginCount());
            tokenResponse.setResponse(response);
            
            return tokenResponse;
        } catch (Exception e) {
            log.error("Login Failed", e);
            return new LoginResponse("Login Failed: " + e.getMessage(), null, null);
        }
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public UpdateResponse updateUser(UUID id, RegisterData request) {
        try {
            Optional<User> optional = userRepository.findById(id);
            if (optional.isEmpty()) {
                return new UpdateResponse("User Not Found", null);
            }
            
            User user = optional.get();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setMobile(request.getMobile());
            if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
                user.setPassword(request.getPassword());
            }
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
            
            return new UpdateResponse("User Updated Successfully", response);
        } catch (Exception e) {
            log.error("Update User Failed", e);
            return new UpdateResponse("Update Failed: " + e.getMessage(), null);
        }
    }

    @Override
    public String deleteUser(UUID id) {
        try {
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
        } catch (Exception e) {
            log.error("Delete User Failed", e);
            return "Delete Failed: " + e.getMessage();
        }
    }

    @Override
    public String uploadPhoto(UUID userId, MultipartFile file) {
        try {
            log.info("Uploading photo for User ID: {}, Original File: {}", userId, file.getOriginalFilename());
            Optional<User> optional = userRepository.findById(userId);
            
            if (optional.isEmpty()) {
                return "User Not Found";
            }
            
            User user = optional.get();
            String fileName = fileStorageService.saveFile(file);
            log.info("Saved file name: {}", fileName);
            
            user.setProfilePhoto(fileName);
            userRepository.save(user);
            
            return "Photo Uploaded Successfully";
        } catch (Exception e) {
            log.error("Photo Upload Failed", e);
            return e.getMessage();
        }
    }
}