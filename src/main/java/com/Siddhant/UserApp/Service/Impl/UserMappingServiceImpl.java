package com.Siddhant.UserApp.Service.Impl;
import com.Siddhant.UserApp.Entity.Status;
import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.Entity.UserMapping;
import com.Siddhant.UserApp.Repository.ForgetUserRepository;
import com.Siddhant.UserApp.Repository.UserMappingRepository;
import com.Siddhant.UserApp.Repository.UserRepository;
import com.Siddhant.UserApp.Service.UserMappingService;
import com.Siddhant.UserApp.dto.UserMappingRequest;
import com.Siddhant.UserApp.dto.UserMappingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Service
public class UserMappingServiceImpl implements UserMappingService {
    private static final Logger log = LoggerFactory.getLogger(UserMappingServiceImpl.class);
    private final UserMappingRepository userMappingRepository;
    private final UserRepository userRepository;
    private final ForgetUserRepository forgetUserRepository;
    @Autowired
    public UserMappingServiceImpl(UserMappingRepository userMappingRepository, 
                                  UserRepository userRepository, 
                                  ForgetUserRepository forgetUserRepository) {
        this.userMappingRepository = userMappingRepository;
        this.userRepository = userRepository;
        this.forgetUserRepository = forgetUserRepository;
    }
    @Override
    public UserMappingResponse saveUserMapping(UserMappingRequest request) {
        try {
            if (request.getUserId() == null) {
                return new UserMappingResponse("User Id is required", null);
            }
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                return new UserMappingResponse("Name is required", null);
            }
            if (!request.getName().matches("^[A-Za-z]+\\s+[A-Za-z]+$")) {
                return new UserMappingResponse("Please enter first name and last name", null);
            }
            if (request.getAddress() == null || request.getAddress().trim().isEmpty()) {
                return new UserMappingResponse("Address is required", null);
            }
            if (request.getPincode() == null || request.getPincode().trim().isEmpty()) {
                return new UserMappingResponse("Pincode is required", null);
            }
            if (!request.getPincode().matches("^\\d{6}$")) {
                return new UserMappingResponse("Please enter valid 6 digit pincode", null);
            }
            if (request.getMobileNo() == null || request.getMobileNo().trim().isEmpty()) {
                return new UserMappingResponse("Mobile number is required", null);
            }
            if (!request.getMobileNo().matches("^[6-9]\\d{9}$")) {
                return new UserMappingResponse("Please enter valid 10 digit mobile number", null);
            }
            if (request.getCity() == null || request.getCity().trim().isEmpty()) {
                return new UserMappingResponse("City is required", null);
            }
            if (request.getState() == null || request.getState().trim().isEmpty()) {
                return new UserMappingResponse("State is required", null);
            }
            Optional<User> optionalUser = userRepository.findById(request.getUserId());
            if (optionalUser.isEmpty()) {
                return new UserMappingResponse("User Not Found", null);
            }
            UserMapping userMapping = new UserMapping();
            userMapping.setUser(optionalUser.get());
            userMapping.setName(request.getName());
            userMapping.setAddress(request.getAddress());
            userMapping.setPincode(request.getPincode());
            userMapping.setMobileNo(request.getMobileNo());
            userMapping.setCity(request.getCity());
            userMapping.setState(request.getState());
            userMapping.setCreatedBy("Admin");
            userMapping.setCreatedOn(LocalDateTime.now());
            userMapping.setUpdatedBy("Admin");
            userMapping.setUpdatedOn(LocalDateTime.now());
            userMapping.setIsActive(true);
            userMapping.setIsDelete(false);
            userMapping.setStatus(Status.ACTIVE);
            userMappingRepository.save(userMapping);
            log.info("User mapping saved for user ID: {}", request.getUserId());
            UserMappingRequest response = new UserMappingRequest();
            response.setUserId(userMapping.getUser().getUserId());
            response.setName(userMapping.getName());
            response.setAddress(userMapping.getAddress());
            response.setPincode(userMapping.getPincode());
            response.setMobileNo(userMapping.getMobileNo());
            response.setCity(userMapping.getCity());
            response.setState(userMapping.getState());
            response.setCreatedBy(userMapping.getCreatedBy());
            response.setCreatedOn(userMapping.getCreatedOn());
            response.setUpdatedBy(userMapping.getUpdatedBy());
            response.setUpdatedOn(userMapping.getUpdatedOn());
            response.setIsActive(userMapping.getIsActive());
            response.setIsDelete(userMapping.getIsDelete());
            response.setStatus(userMapping.getStatus());
            return new UserMappingResponse("User Mapping Saved Successfully", response);
        } catch (Exception e) {
            log.error("Error saving user mapping", e);
            return new UserMappingResponse(e.getMessage(), null);
        }
    }
    @Override
    public UserMappingResponse updateUserMapping(UUID id, UserMappingRequest request) {
        try {
            if (request.getName() != null && !request.getName().matches("^[A-Za-z]+\\s+[A-Za-z]+$")) {
                return new UserMappingResponse("Please enter first name and last name", null);
            }
            if (request.getPincode() != null && !request.getPincode().matches("^\\d{6}$")) {
                return new UserMappingResponse("Please enter valid 6 digit pincode", null);
            }
            if (request.getMobileNo() != null && !request.getMobileNo().matches("^[6-9]\\d{9}$")) {
                return new UserMappingResponse("Please enter valid 10 digit mobile number", null);
            }
            Optional<UserMapping> optionalMapping = userMappingRepository.findById(id);
            if (optionalMapping.isEmpty()) {
                return new UserMappingResponse("User Mapping Not Found", null);
            }
            UserMapping userMapping = optionalMapping.get();
            if (request.getUserId() != null) {
                Optional<User> optionalUser = userRepository.findById(request.getUserId());
                if (optionalUser.isEmpty()) {
                    return new UserMappingResponse("User Not Found", null);
                }
                userMapping.setUser(optionalUser.get());
            }
            userMapping.setName(request.getName() != null && !request.getName().isEmpty() ? request.getName() : userMapping.getName());
            userMapping.setAddress(request.getAddress() != null && !request.getAddress().isEmpty() ? request.getAddress() : userMapping.getAddress());
            userMapping.setPincode(request.getPincode() != null && !request.getPincode().isEmpty() ? request.getPincode() : userMapping.getPincode());
            userMapping.setMobileNo(request.getMobileNo() != null && !request.getMobileNo().isEmpty() ? request.getMobileNo() : userMapping.getMobileNo());
            userMapping.setCity(request.getCity() != null && !request.getCity().isEmpty() ? request.getCity() : userMapping.getCity());
            userMapping.setState(request.getState() != null && !request.getState().isEmpty() ? request.getState() : userMapping.getState());
            userMapping.setUpdatedBy("Admin");
            userMapping.setUpdatedOn(LocalDateTime.now());
            userMapping.setIsActive(true);
            userMapping.setIsDelete(false);
            userMapping.setStatus(Status.ACTIVE);
            userMappingRepository.save(userMapping);
            log.info("User mapping updated for ID: {}", id);
            UserMappingRequest response = new UserMappingRequest();
            response.setUserId(userMapping.getUser().getUserId());
            response.setName(userMapping.getName());
            response.setAddress(userMapping.getAddress());
            response.setPincode(userMapping.getPincode());
            response.setMobileNo(userMapping.getMobileNo());
            response.setCity(userMapping.getCity());
            response.setState(userMapping.getState());
            response.setCreatedBy(userMapping.getCreatedBy());
            response.setCreatedOn(userMapping.getCreatedOn());
            response.setUpdatedBy(userMapping.getUpdatedBy());
            response.setUpdatedOn(userMapping.getUpdatedOn());
            response.setIsActive(userMapping.getIsActive());
            response.setIsDelete(userMapping.getIsDelete());
            response.setStatus(userMapping.getStatus());
            return new UserMappingResponse("User Mapping Updated Successfully", response);
        } catch (Exception e) {
            log.error("Error updating user mapping", e);
            return new UserMappingResponse(e.getMessage(), null);
        }
    }
    @Override
    public List<UserMappingResponse> getAllUserMappings() {
        return List.of();
    }
    @Override
    public UserMappingResponse getUserMappingById(UUID id) {
        return null;
    }
    @Override
    public String deleteUserMapping(UUID id) {
        return "";
    }
}