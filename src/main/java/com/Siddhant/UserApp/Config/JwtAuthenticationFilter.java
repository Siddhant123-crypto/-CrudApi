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
import java.util.Collections;
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
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
                || path.startsWith("/auth/")) {
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
                if (jwtService.validateToken(token, username)) {SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.toUpperCase());
                    UsernamePasswordAuthenticationToken
                            authToken = new UsernamePasswordAuthenticationToken(
                                    username, null,
                                    Collections.singletonList(authority));
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("JWT Authentication Successful");
                    System.out.println("Username : " + username);
                    System.out.println("Role : " + role);
                }
            }
        } catch (Exception e) {
            System.out.println("JWT Authentication Failed : " + e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}