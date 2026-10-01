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
import com.Siddhant.UserApp.Service.NotificationService;
import com.Siddhant.UserApp.dto.NotificationSendRequest;
import com.Siddhant.UserApp.Entity.Role;
import com.Siddhant.UserApp.enums.SupportRequestStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class SupportRequestServiceImpl implements SupportRequestService {

    private final SupportRequestRepository supportRequestRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final NotificationService notificationService;

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
        response.setAssignedAdminId(request.getAssignedAdminId());
        response.setHasUnreadTransfer(request.getHasUnreadTransfer());
        
        if (request.getTransferredByAdminId() != null) {
            adminRepository.findById(request.getTransferredByAdminId())
                    .ifPresent(admin -> response.setTransferredByAdminName(admin.getName()));
        }
        
        return response;
    }

    @Override
    public SupportRequestResponse createSupportRequest(SupportRequestRequest request) {
        User user = getCurrentUser();

        // Prevent duplicate active support requests
        List<SupportRequest> existingRequests = supportRequestRepository.findByUserOrderByCreatedAtDesc(user);
        for (SupportRequest req : existingRequests) {
            if (req.getStatus() == com.Siddhant.UserApp.enums.SupportRequestStatus.OPEN || 
                req.getStatus() == com.Siddhant.UserApp.enums.SupportRequestStatus.IN_PROGRESS) {
                return mapToResponse(req);
            }
        }

        SupportRequest supportRequest = new SupportRequest();
        supportRequest.setUser(user);
        supportRequest.setSubject(request.getSubject());
        supportRequest.setDescription(request.getDescription());
        supportRequest.setStatus(com.Siddhant.UserApp.enums.SupportRequestStatus.OPEN);

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

    @Override
    public List<SupportRequestResponse> getUnassignedSupportRequests() {
        Object principal = getCurrentPrincipal();
        if (!(principal instanceof Admin)) {
            throw new RuntimeException("Only admins can access support requests");
        }
        return supportRequestRepository.findByAssignedAdminIdIsNullAndStatusNotOrderByUpdatedAtDesc(SupportRequestStatus.RESOLVED)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SupportRequestResponse> getMyAssignedSupportRequests() {
        Object principal = getCurrentPrincipal();
        if (!(principal instanceof Admin)) {
            throw new RuntimeException("Only admins can access support requests");
        }
        Admin admin = (Admin) principal;
        return supportRequestRepository.findByAssignedAdminIdOrderByUpdatedAtDesc(admin.getId())
                .stream()
                .filter(req -> req.getStatus() != SupportRequestStatus.RESOLVED)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SupportRequestResponse> getResolvedSupportRequests() {
        Object principal = getCurrentPrincipal();
        if (!(principal instanceof Admin)) {
            throw new RuntimeException("Only admins can access support requests");
        }
        return supportRequestRepository.findByStatusOrderByUpdatedAtDesc(SupportRequestStatus.RESOLVED)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SupportRequestResponse assignToMe(UUID id) {
        Object principal = getCurrentPrincipal();
        if (!(principal instanceof Admin)) {
            throw new RuntimeException("Only admins can assign support requests");
        }
        Admin admin = (Admin) principal;
        
        int rowsAffected = supportRequestRepository.claimSupportRequest(id, admin.getId(), SupportRequestStatus.IN_PROGRESS);
        if (rowsAffected == 0) {
            throw new RuntimeException("Conversation already assigned to another admin or does not exist");
        }
        
        SupportRequest updated = supportRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Support request not found"));
                
        // Notify other admins about the assignment
        try {
            List<Admin> otherAdmins = adminRepository.findAll().stream()
                .filter(a -> !a.getId().equals(admin.getId()))
                .collect(Collectors.toList());
                
            String farmerName = updated.getUser().getName() != null ? updated.getUser().getName() : "Farmer";
            for (Admin otherAdmin : otherAdmins) {
                NotificationSendRequest notifReq = new NotificationSendRequest();
                notifReq.setReceiverId(otherAdmin.getId());
                notifReq.setReceiverType(Role.ADMIN.name());
                notifReq.setTitle("Farmer Assigned");
                notifReq.setMessage(admin.getName() + " assigned " + farmerName + " to themselves.");
                notifReq.setNotificationType(com.Siddhant.UserApp.enums.NotificationType.ADMIN);
                notificationService.sendNotification(notifReq);
            }
        } catch (Exception e) {
            // log error
        }

        return mapToResponse(updated);
    }

    @Override
    public SupportRequestResponse transferToAdmin(UUID id, UUID newAdminId) {
        Object principal = getCurrentPrincipal();
        if (!(principal instanceof Admin)) {
            throw new RuntimeException("Only admins can transfer support requests");
        }
        
        SupportRequest supportRequest = supportRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Support request not found"));
                
        Admin currentAdmin = (Admin) principal;
        
        if (supportRequest.getAssignedAdminId() == null) {
            throw new RuntimeException("Cannot transfer an unassigned support request");
        }
        if (!supportRequest.getAssignedAdminId().equals(currentAdmin.getId())) {
            throw new RuntimeException("Not authorized to transfer this support request. Only the assigned admin can transfer it.");
        }
        if (currentAdmin.getId().equals(newAdminId)) {
            throw new RuntimeException("Cannot transfer to yourself");
        }
                
        Admin newAdmin = adminRepository.findById(newAdminId)
                .orElseThrow(() -> new RuntimeException("Target admin not found"));
                
        supportRequest.setAssignedAdminId(newAdmin.getId());
        supportRequest.setTransferredByAdminId(currentAdmin.getId());
        supportRequest.setHasUnreadTransfer(true);
        supportRequest.setStatus(com.Siddhant.UserApp.enums.SupportRequestStatus.IN_PROGRESS);
        SupportRequest updated = supportRequestRepository.save(supportRequest);
        
        // Notify target admin about the transfer
        try {
            String farmerName = updated.getUser().getName() != null ? updated.getUser().getName() : "Farmer";
            NotificationSendRequest notifReq = new NotificationSendRequest();
            notifReq.setReceiverId(newAdmin.getId());
            notifReq.setReceiverType(Role.ADMIN.name());
            notifReq.setTitle("Farmer Transferred");
            notifReq.setMessage(((Admin) principal).getName() + " transferred " + farmerName + " to you.");
            notifReq.setNotificationType(com.Siddhant.UserApp.enums.NotificationType.ADMIN);
            notificationService.sendNotification(notifReq);
        } catch (Exception e) {
            // log error
        }
        
        return mapToResponse(updated);
    }

    @Override
    public SupportRequestResponse resolveSupportRequest(UUID id) {
        Object principal = getCurrentPrincipal();
        if (!(principal instanceof Admin)) {
            throw new RuntimeException("Only admins can resolve support requests");
        }
        Admin admin = (Admin) principal;
        
        SupportRequest supportRequest = supportRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Support request not found"));
                
        supportRequest.setStatus(SupportRequestStatus.RESOLVED);
        supportRequest.setResolvedBy(admin.getId());
        supportRequest.setResolvedAt(java.time.LocalDateTime.now());
        
        SupportRequest updated = supportRequestRepository.save(supportRequest);
        return mapToResponse(updated);
    }
}
