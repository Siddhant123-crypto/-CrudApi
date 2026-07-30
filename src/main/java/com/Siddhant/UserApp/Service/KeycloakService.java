package com.Siddhant.UserApp.Service;

import com.Siddhant.UserApp.dto.LoginResponse;

public interface KeycloakService {

    LoginResponse getAccessToken(String username, String password);
    void createUser(String firstName,
                    String lastName,
                    String email,
                    String password);


}
