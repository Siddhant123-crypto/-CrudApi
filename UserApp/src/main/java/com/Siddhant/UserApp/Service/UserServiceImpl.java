package com.Siddhant.UserApp.Service;

import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.Repository.UserRepository;
import com.Siddhant.UserApp.Service.UserService;
import com.Siddhant.UserApp.dto.LoginRequest;
import com.Siddhant.UserApp.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public String register(RegisterRequest request) {

        try {

            // Name Validation
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                return "Name is required";
            }

            if (!request.getName().matches("^[A-Za-z]+\\s+[A-Za-z]+$")) {
                return "Please enter first name and last name";
            }

            // Email Validation
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return "Email is required";
            }

            if (!Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", request.getEmail())) {
                return "Please enter valid email";
            }

            // Mobile Validation
            if (request.getMobile() == null || request.getMobile().trim().isEmpty()) {
                return "Mobile number is required";
            }

            if (!request.getMobile().matches("^[6-9]\\d{9}$")) {
                return "Please enter 10 digit mobile number";
            }

            // Password Validation
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return "Password is required";
            }

            if (!request.getPassword().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{6,}$")) {
                return "Password must contain uppercase, lowercase, number and special character";
            }

            // State Validation
            if (request.getState() == null || request.getState().trim().isEmpty()) {
                return "State is required";
            }

            // Email Duplicate
            Optional<User> email = userRepository.findByEmail(request.getEmail());

            if (email.isPresent()) {
                return "Email already exists";
            }

            // Mobile Duplicate
            Optional<User> mobile = userRepository.findByMobile(request.getMobile());

            if (mobile.isPresent()) {
                return "Mobile number already exists";
            }

            User user = new User();

            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setMobile(request.getMobile());
            user.setPassword(request.getPassword());
            user.setState(request.getState());

            user.setIsActive(true);
            user.setIsDelete(false);

            user.setCreatedOn(LocalDateTime.now());
            user.setUpdatedOn(LocalDateTime.now());

            user.setCreatedBy("Admin");
            user.setUpdatedBy("Admin");

            userRepository.save(user);

            return "Registration Successful";

        } catch (Exception e) {

            return e.getMessage();

        }

    }

    @Override
    public String login(LoginRequest request) {

        try {

            Optional<User> optional = userRepository.findByEmail(request.getEmail());

            if (optional.isEmpty()) {
                return "Email not found";
            }

            User user = optional.get();

            if (!user.getPassword().equals(request.getPassword())) {
                return "Incorrect Password";
            }

            if (!user.getIsActive()) {
                return "User Account is Inactive";
            }

            if (user.getIsDelete()) {
                return "User Account Deleted";
            }

            user.setLastLogin(LocalDateTime.now());

            userRepository.save(user);

            return "Login Successful";

        } catch (Exception e) {

            return e.getMessage();

        }

    }

    @Override
    public List<User> getAllUsers() {

        return userRepository.findAll();

    }

    @Override
    public User getUserById(Integer id) {

        Optional<User> optional = userRepository.findById(id);

        if (optional.isPresent()) {

            return optional.get();

        }

        return null;

    }

    @Override
    public String updateUser(Integer id, RegisterRequest request) {

        Optional<User> optional = userRepository.findById(id);

        if (optional.isEmpty()) {

            return "User Not Found";

        }

        User user = optional.get();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());
        user.setPassword(request.getPassword());
        user.setState(request.getState());

        user.setUpdatedOn(LocalDateTime.now());
        user.setUpdatedBy("Admin");

        userRepository.save(user);

        return "User Updated Successfully";

    }

    @Override
    public String deleteUser(Integer id) {

        Optional<User> optional = userRepository.findById(id);

        if (optional.isEmpty()) {

            return "User Not Found";

        }

        User user = optional.get();

        user.setIsDelete(true);
        user.setIsActive(false);

        userRepository.save(user);

        return "User Deleted Successfully";

    }

}
