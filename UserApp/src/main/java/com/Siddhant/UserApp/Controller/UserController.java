package com.Siddhant.UserApp.Controller;

import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.Service.UserService;
import com.Siddhant.UserApp.dto.LoginRequest;
import com.Siddhant.UserApp.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // Register
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {

        return userService.register(request);

    }

    // Login
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        return userService.login(request);

    }

    // Get All Users
    @GetMapping("/getAll")
    public List<User> getAllUsers() {

        return userService.getAllUsers();

    }

    // Get User By Id
    @GetMapping("/getById/{id}")
    public User getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    // Update User
    @PutMapping("/update/{id}")
    public String updateUser(@PathVariable Integer id,
                             @RequestBody RegisterRequest request) {

        return userService.updateUser(id, request);

    }

    // Delete User (Soft Delete)
    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable Integer id) {

        return userService.deleteUser(id);

    }

}
