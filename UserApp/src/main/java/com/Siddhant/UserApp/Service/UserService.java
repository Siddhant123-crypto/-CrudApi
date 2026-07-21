package com.Siddhant.UserApp.Service;

import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService {

    RegisterResponse register(RegisterData request);

    LoginResponse login(LoginRequest request);

    List<User> getAllUsers();

    UpdateResponse updateUser(UUID id, RegisterData request);

    User getUserById(UUID id);

    String deleteUser(UUID id);

    String uploadPhoto(UUID userId, MultipartFile file);
}