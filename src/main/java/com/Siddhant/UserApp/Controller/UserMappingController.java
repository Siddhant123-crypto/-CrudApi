package com.Siddhant.UserApp.Controller;
import com.Siddhant.UserApp.Service.UserMappingService;
import com.Siddhant.UserApp.dto.UserMappingRequest;
import com.Siddhant.UserApp.dto.UserMappingResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController
@RequestMapping("/userMapping")
@CrossOrigin("*")
public class UserMappingController {
    private static final Logger log = LoggerFactory.getLogger(UserMappingController.class);
    private final UserMappingService userMappingService;
    @Autowired
    public UserMappingController(UserMappingService userMappingService) {
        this.userMappingService = userMappingService;
    }
    @PostMapping("/save")
    public UserMappingResponse saveUserMapping(@RequestBody UserMappingRequest request) {
        log.debug("Saving User Mapping for User ID: {}", request.getUserId());
        return userMappingService.saveUserMapping(request);
    }
    @PutMapping("/update/{id}")
    public UserMappingResponse updateUserMapping(
            @PathVariable UUID id,
            @RequestBody UserMappingRequest request) {
        log.debug("Updating User Mapping for ID: {}", id);
        return userMappingService.updateUserMapping(id, request);
    }
}
