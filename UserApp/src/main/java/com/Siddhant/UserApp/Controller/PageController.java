package com.Siddhant.UserApp.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/register")
    public String registrationPage() {
        return "registation";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/home")
    public String homePage() {
        return "home";
    }

    @GetMapping("/customer")
    public String customer(){
        return "customer";
    }


    @GetMapping("/farmer")
    public String farmer(){
        return "farmer";
    }

}
