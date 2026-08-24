package com.Siddhant.UserApp.Controller;
import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.Service.FileStorageService;
import com.Siddhant.UserApp.Service.UserService;
import com.Siddhant.UserApp.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;
@Slf4j
@RestController
@RequestMapping("/user")
@CrossOrigin("*")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FileStorageService fileStorageService;
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RegisterResponse register(
            @RequestPart("data") String data,
            @RequestPart(value = "photo", required = false) MultipartFile photo) throws java.io.IOException {
        log.debug("DATA RECEIVED FROM MOBILE = {}", data);
        RegisterData request = new com.fasterxml.jackson.databind.ObjectMapper().readValue(data, RegisterData.class);
        return userService.register(request, photo);
    }
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }
    @GetMapping("/getAll")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    @GetMapping("/getById/{id}")
    public User getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }
    @PutMapping("/update/{id}")
    public UpdateResponse updateUser(@PathVariable UUID id, @Valid @RequestBody RegisterData request) {
        return userService.updateUser(id, request);
    }
    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable UUID id) {
        return userService.deleteUser(id);
    }
    @PostMapping("/uploadPhoto/{id}")
    public String uploadPhoto(@PathVariable UUID id, @RequestParam("file") MultipartFile file) {
        return userService.uploadPhoto(id, file);
    }
    @GetMapping("/getPhoto/{id}")
    public String getPhoto(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        return user.getProfilePhoto();
    }
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