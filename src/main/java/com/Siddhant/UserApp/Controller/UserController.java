package com.Siddhant.UserApp.Controller;

import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.Service.FileStorageService;
import com.Siddhant.UserApp.Service.UserService;
import com.Siddhant.UserApp.dto.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    // Register
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RegisterResponse register(
            @RequestParam("data") String data,
            @RequestPart(value = "photo", required = false) MultipartFile photo) throws java.io.IOException {
        RegisterData request = new com.fasterxml.jackson.databind.ObjectMapper().readValue(data, RegisterData.class);
        return userService.register(request, photo);
    }

    // Login
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        return userService.login(request);

    }

    // Get All Users
    @GetMapping("/getAll")
    public List<User> getAllUsers() {

        return userService.getAllUsers();

    }

    // Get User By Id
    @GetMapping("/getById/{id}")
    public User getUserById(@PathVariable UUID id) {

        return userService.getUserById(id);

    }

    // Update User
    @PutMapping("/update/{id}")
    public UpdateResponse updateUser(
            @PathVariable UUID id,
            @RequestBody RegisterData request) {

        return userService.updateUser(id, request);

    }

    // Delete User
    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable UUID id) {

        return userService.deleteUser(id);

    }

    // Upload Profile Photo
    @PostMapping("/uploadPhoto/{id}")
    public String uploadPhoto(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {

        return userService.uploadPhoto(id, file);

    }

    // Get Photo Name
    @GetMapping("/getPhoto/{id}")
    public String getPhoto(@PathVariable UUID id) {

        User user = userService.getUserById(id);

        return user.getProfilePhoto();

    }

    // Show Image
    @GetMapping("/image/{fileName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String fileName) {

        byte[] image = fileStorageService.getFile(fileName);

        if (image == null) {

            return ResponseEntity.notFound().build();

        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                .body(image);

    }

}