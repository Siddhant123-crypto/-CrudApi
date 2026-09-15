package com.Siddhant.UserApp.Service.Impl;

import com.Siddhant.UserApp.Entity.Admin;
import com.Siddhant.UserApp.Entity.SupportRequest;
import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.Repository.AdminRepository;
import com.Siddhant.UserApp.Repository.SupportRequestRepository;
import com.Siddhant.UserApp.Repository.UserRepository;
import com.Siddhant.UserApp.Service.SupportRequestService;
import com.Siddhant.UserApp.dto.SupportRequestRequest;
import com.Siddhant.UserApp.dto.SupportRequestResponse;
import com.Siddhant.UserApp.enums.SupportRequestStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupportRequestServiceImpl implements SupportRequestService {

    private final SupportRequestRepository supportRequestRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    private Object getCurrentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        String username = authentication.getName();

        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {

            return adminRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("Admin not found"));

        } else {
            return userRepository.findFirstByEmail(username)
                    .orElseGet(() -> userRepository.findFirstByMobile(username)
                            .orElseThrow(() -> new RuntimeException("User not found")));
        }
    }

    private User getCurrentUser() {
        Object principal = getCurrentPrincipal();
        if (principal instanceof Admin) {
            throw new RuntimeException("Admins cannot create or view standard support requests using this endpoint");
        }
        return (User) principal;
    }

    private SupportRequestResponse mapToResponse(SupportRequest request) {
        SupportRequestResponse response = new SupportRequestResponse();
        response.setSupportRequestId(request.getSupportRequestId());
        response.setUserId(request.getUser().getUserId());
        response.setUserRole(request.getUser().getRole().name());
        response.setUserName(request.getUser().getName());
        response.setSubject(request.getSubject());
        response.setDescription(request.getDescription());
        response.setStatus(request.getStatus());
        response.setCreatedAt(request.getCreatedAt());
        response.setUpdatedAt(request.getUpdatedAt());
        return response;
    }

    @Override
    public SupportRequestResponse createSupportRequest(SupportRequestRequest request) {
        User user = getCurrentUser();

        SupportRequest supportRequest = new SupportRequest();
        supportRequest.setUser(user);
        supportRequest.setSubject(request.getSubject());
        supportRequest.setDescription(request.getDescription());

        SupportRequest saved = supportRequestRepository.save(supportRequest);
        return mapToResponse(saved);
    }

    @Override
    public List<SupportRequestResponse> getMySupportRequests() {
        User user = getCurrentUser();
        return supportRequestRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SupportRequestResponse getMySupportRequestById(UUID id) {
        User user = getCurrentUser();
        SupportRequest supportRequest = supportRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Support request not found"));

        if (!supportRequest.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Unauthorized to access this support request");
        }

        return mapToResponse(supportRequest);
    }

    // Admin endpoints
    @Override
    public List<SupportRequestResponse> getAllSupportRequests() {
        // Ensure admin
        Object principal = getCurrentPrincipal();
        if (!(principal instanceof Admin)) {
            throw new RuntimeException("Only admins can access all support requests");
        }

        return supportRequestRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SupportRequestResponse getSupportRequestById(UUID id) {
        Object principal = getCurrentPrincipal();
        if (!(principal instanceof Admin)) {
            throw new RuntimeException("Only admins can access all support requests");
        }

        SupportRequest supportRequest = supportRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Support request not found"));

        return mapToResponse(supportRequest);
    }

    @Override
    public SupportRequestResponse updateSupportRequestStatus(UUID id, SupportRequestStatus status) {
        Object principal = getCurrentPrincipal();
        if (!(principal instanceof Admin)) {
            throw new RuntimeException("Only admins can update support requests");
        }

        SupportRequest supportRequest = supportRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Support request not found"));

        supportRequest.setStatus(status);
        SupportRequest updated = supportRequestRepository.save(supportRequest);

        return mapToResponse(updated);
    }
}
