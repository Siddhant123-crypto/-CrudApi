package com.Siddhant.UserApp.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());

        BearerTokenResolver defaultResolver = new DefaultBearerTokenResolver();
        BearerTokenResolver customBearerTokenResolver = request -> {
            String path = request.getServletPath();
            if (path.equals("/farmer/save") ||
                path.equals("/farmer/login") ||
                    path.startsWith("/farmer/state/") ||
                    path.equals("/farmer/nearby") ||
                path.equals("/user/register") ||
                path.equals("/user/login") ||
                path.startsWith("/user/image/") ||
                path.startsWith("/user/getPhoto/") ||
                path.startsWith("/product/"))
                {
                return null;
            }
            return defaultResolver.resolve(request);
        };

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // Public endpoints
                        .requestMatchers(
                                "/user/register",
                                "/user/login",
                                "/user/image/**",
                                "/farmer/save",
                                "/farmer/login",
                                "/farmer/state/**",
                                "/farmer/nearby",
                                "/product/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET, "/user/getPhoto/**")
                        .permitAll()

                        // Remaining APIs
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .bearerTokenResolver(customBearerTokenResolver)
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                );

        return http.build();
    }
}
