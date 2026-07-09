package com.Siddhant.UserApp.Controller;

import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.Service.UserService;
import com.Siddhant.UserApp.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // Register
    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterData request) {

        return userService.register(request);

    }
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }
    // Get All Users
    @GetMapping("/getAll")
    public List<User> getAllUsers() {

        return userService.getAllUsers();

    }

    // Get User By UUID
    @GetMapping("/getById/{id}")
    public User getUserById(@PathVariable UUID id) {

        return userService.getUserById(id);

    }

    // Update User
    @PutMapping("/update/{id}")
    public UpdateResponse updateUser(@PathVariable UUID id,
                                     @RequestBody RegisterData request) {

        return userService.updateUser(id, request);
    }
    // Delete User (Soft Delete)
    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable UUID id) {

        return userService.deleteUser(id);

    }

}