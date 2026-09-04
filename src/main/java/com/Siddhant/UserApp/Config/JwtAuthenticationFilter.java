package com.Siddhant.UserApp.Config;
import com.Siddhant.UserApp.Service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import com.Siddhant.UserApp.Repository.UserRepository;
import com.Siddhant.UserApp.Repository.AdminRepository;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository, AdminRepository adminRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {String path = request.getRequestURI();System.out.println("================================");System.out.println("REQUEST : " + request.getMethod());System.out.println("URL     : " + path);
        // =========================
        // PUBLIC APIs
        // =========================
        if (path.equals("/user/login")
                || path.equals("/user/register")
                || path.equals("/user/google-login")
                || path.equals("/newuser/google-login")
                || path.equals("/newuser/login")
                || path.equals("/farmer/save")
                || path.equals("/farmer/login")
                || path.startsWith("/farmer/state/")
                || path.equals("/farmer/nearby")
                || path.startsWith("/user/image/")
                || path.startsWith("/user/getPhoto/")
                || path.startsWith("/product/image/")
                || path.startsWith("/auth/")
                || path.equals("/admin/login")) {
            System.out.println("PUBLIC API - JWT FILTER SKIPPED");
            filterChain.doFilter(request, response);
            return;
        }
        // =========================
        // GET AUTHORIZATION HEADER
        // =========================
        String authHeader = request.getHeader("Authorization");
        System.out.println("AUTH HEADER : " + authHeader);
        // No token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("NO JWT TOKEN");
            filterChain.doFilter(request, response);
            return;
        }
        // Remove "Bearer "
        String token = authHeader.substring(7);
        try {
            // =========================
            // EXTRACT USERNAME + ROLE
            // =========================
            String username = jwtService.extractUsername(token);
            String role = jwtService.extractRole(token);
            
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // =========================
                // VALIDATE TOKEN
                // =========================
                if (jwtService.validateToken(token, username)) {
                    boolean isValidUser = false;
                    
                    if ("ADMIN".equalsIgnoreCase(role)) {
                        isValidUser = adminRepository.findByEmail(username)
                            .map(admin -> admin.getIsActive() != null && admin.getIsActive() && (admin.getIsDelete() == null || !admin.getIsDelete()))
                            .orElse(false);
                    } else if ("CUSTOMER".equalsIgnoreCase(role) || "FARMER".equalsIgnoreCase(role)) {
                        isValidUser = userRepository.findByEmail(username)
                            .map(user -> user.getIsActive() != null && user.getIsActive() && (user.getIsDelete() == null || !user.getIsDelete()))
                            .orElse(false);
                    }
                    
                    if (isValidUser) {
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                username, null, Collections.singletonList(authority));
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        
                        System.out.println("JWT Authentication Successful");
                        System.out.println("Username : " + username);
                        System.out.println("Role : " + role);
                    } else {
                        System.out.println("JWT Authentication Failed: User/Admin not found or inactive");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("JWT Authentication Failed : " + e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}