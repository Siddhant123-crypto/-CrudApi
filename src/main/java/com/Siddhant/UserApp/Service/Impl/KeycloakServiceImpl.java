package com.Siddhant.UserApp.Service.Impl;

import com.Siddhant.UserApp.Service.KeycloakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.Siddhant.UserApp.dto.LoginResponse;
import java.util.HashMap;
import java.util.List;

import java.util.Map;

@Service
public class KeycloakServiceImpl implements KeycloakService {

    @Autowired
    private RestTemplate restTemplate;


    @Override
    public LoginResponse getAccessToken(String username, String password) {

        String url = "http://localhost:9090/realms/farm2home/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", "farm2home-app");
        body.add("username", username);
        body.add("password", password);

        HttpEntity<MultiValueMap<String, String>> entity =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, entity, Map.class);

        Map<String, Object> map = response.getBody();

        LoginResponse loginResponse = new LoginResponse();

        loginResponse.setAccessToken((String) map.get("access_token"));
        loginResponse.setRefreshToken((String) map.get("refresh_token"));
        loginResponse.setExpiresIn(((Number) map.get("expires_in")).intValue());
        loginResponse.setRefreshExpiresIn(((Number) map.get("refresh_expires_in")).intValue());

        return loginResponse;
    }
    @Override
    public void createUser(String firstName,
                           String lastName,
                           String email,
                           String password) {

        try {

            String token = getAdminAccessToken();

            System.out.println("Admin Token = " + token);

            String url = "http://localhost:9090/admin/realms/farm2home/users";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> credential = Map.of(
                    "type", "password",
                    "value", password,
                    "temporary", false
            );

            Map<String, Object> body = Map.of(
                    "username", email,
                    "email", email,
                    "firstName", firstName,
                    "lastName", lastName,
                    "enabled", true,
                    "credentials", List.of(credential)
            );

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(url, entity, String.class);

            System.out.println("Status = " + response.getStatusCode());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String getAdminAccessToken() {

        String url = "http://localhost:9090/realms/master/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", "admin-cli");
        body.add("username", "sidd");
        body.add("password", "sidd123");

        HttpEntity<MultiValueMap<String, String>> entity =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, entity, Map.class);

        return (String) response.getBody().get("access_token");
    }


}
