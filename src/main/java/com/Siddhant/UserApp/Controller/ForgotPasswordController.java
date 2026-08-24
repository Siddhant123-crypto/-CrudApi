package com.Siddhant.UserApp.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.Siddhant.UserApp.Service.ForgotPasswordService;
import com.Siddhant.UserApp.dto.ForgotPasswordRequest;
@RestController
@RequestMapping("/forgotpassword")
@CrossOrigin("*")
public class ForgotPasswordController {
    private final ForgotPasswordService forgotPasswordService;
    @Autowired
    public ForgotPasswordController(ForgotPasswordService forgotPasswordService) {
        this.forgotPasswordService = forgotPasswordService;
    }
    @PostMapping
    public String forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return forgotPasswordService.updatePassword(request);
    }
}
