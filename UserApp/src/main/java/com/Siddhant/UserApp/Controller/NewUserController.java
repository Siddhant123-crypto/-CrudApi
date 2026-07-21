package com.Siddhant.UserApp.Controller;

import com.Siddhant.UserApp.Service.NewUserService;
import com.Siddhant.UserApp.dto.NewUserLoginRequest;
import com.Siddhant.UserApp.dto.NewUserLoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/newuser")
public class NewUserController {

    @Autowired
    private NewUserService newUserService;

    @PostMapping("/login")
    public NewUserLoginResponse login(@RequestBody NewUserLoginRequest request) {

        return newUserService.login(request);

    }

}