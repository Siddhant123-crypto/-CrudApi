package com.Siddhant.UserApp.Controller;
import com.Siddhant.UserApp.Service.NewUserService;
import java.util.Map;
import com.Siddhant.UserApp.dto.NewUserLoginRequest;
import com.Siddhant.UserApp.dto.NewUserLoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/newuser")
@CrossOrigin("*")
public class NewUserController {
    private final NewUserService newUserService;
    @Autowired
    public NewUserController(NewUserService newUserService) {
        this.newUserService = newUserService;
    }
    @PostMapping("/login")
    public <NewUserLoginResponse> NewUserLoginResponse login(@RequestBody NewUserLoginRequest request) {
        return (NewUserLoginResponse) newUserService.login(request);
    }
    @PostMapping("/google-login")
    public NewUserLoginResponse googleLogin(@RequestBody Map<String, String> body) {
        return newUserService.googleLogin(body.get("idToken"));
    }
}