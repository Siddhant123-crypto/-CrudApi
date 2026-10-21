package com.Siddhant.UserApp.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Authentication required or token is invalid/expired\"}");
                })
            )
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/user/register", "/user/login", "/user/google-login",
                            "/user/forgot-password", "/user/reset-password", "/user/reset-password-page",
                            "/newuser/google-login", "/newuser/login",
                            "/farmer/save", "/farmer/login", "/farmer/state/**", "/farmer/nearby",
                            "/user/image/**", "/user/getPhoto/**",
                            "/product/image/**", "/product/video/**",
                            "/auth/**",
                            "/uploads/**",  // Allow public access to all uploaded media files (farm images, videos, etc.)
                            "/admin/auth/login",  // Admin login is public
                            "/admin/auth/register", // Admin register is unprotected for Postman setup
                            "/api/payment/webhook"
                        ).permitAll()
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            ).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
