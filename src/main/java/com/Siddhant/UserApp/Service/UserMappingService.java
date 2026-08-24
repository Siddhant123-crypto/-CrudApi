package com.Siddhant.UserApp.Service;
import com.Siddhant.UserApp.dto.UserMappingRequest;
import com.Siddhant.UserApp.dto.UserMappingResponse;

import java.util.List;
import java.util.UUID;
public interface UserMappingService {
    UserMappingResponse saveUserMapping(UserMappingRequest request);
    UserMappingResponse updateUserMapping(UUID id, UserMappingRequest request);
    List<UserMappingResponse> getAllUserMappings();
    UserMappingResponse getUserMappingById(UUID id);
    String deleteUserMapping(UUID id);
}