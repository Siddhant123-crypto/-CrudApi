package com.Siddhant.UserApp.Service;

import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.dto.LoginRequest;
import com.Siddhant.UserApp.dto.RegisterRequest;

import java.util.List;

public interface UserService {

    String register(RegisterRequest request);

    String login(LoginRequest request);

    List<User> getAllUsers();

    User getUserById(Integer id);

    String updateUser(Integer id, RegisterRequest request);

    String deleteUser(Integer id);

}
