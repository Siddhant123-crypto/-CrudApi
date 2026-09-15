package com.Siddhant.UserApp.Service.Impl;
import com.Siddhant.UserApp.Entity.Admin;
import com.Siddhant.UserApp.Entity.Notification;
import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.Repository.AdminRepository;
import com.Siddhant.UserApp.Repository.NotificationRepository;
import com.Siddhant.UserApp.Repository.UserRepository;
import com.Siddhant.UserApp.Service.NotificationService;
import com.Siddhant.UserApp.dto.NotificationResponse;
import com.Siddhant.UserApp.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import com.Siddhant.UserApp.dto.NotificationSendRequest;
import com.Siddhant.UserApp.Entity.Role;
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    @Override
    public NotificationResponse createNotification(UUID userId, String title, String message, NotificationType notificationType) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationType(notificationType);
        Notification saved = notificationRepository.save(notification);
        return mapToResponse(saved);
    }@Override
    public NotificationResponse createAdminNotification(UUID adminId, String title, String message, NotificationType notificationType) {
        Admin admin = adminRepository.findById(adminId).orElseThrow(() -> new RuntimeException("Admin not found"));
        Notification notification = new Notification();
        notification.setAdmin(admin);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationType(notificationType);
        Notification saved = notificationRepository.save(notification);
        return mapToResponse(saved);
    }@Override
    public List<NotificationResponse> getMyNotifications() {
        Object principal = getCurrentPrincipal();
        if (principal instanceof Admin) {
            return notificationRepository.findByAdminOrderByCreatedAtDesc((Admin) principal)
                    .stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } else {User user = (User) principal;
            System.out.println("FETCHING NOTIFICATIONS FOR USER = " + user.getUserId());
            List<Notification> notifications =notificationRepository.findByUserOrderByCreatedAtDesc(user);
            System.out.println("NOTIFICATION COUNT = " + notifications.size());
            for (Notification notification : notifications) {
                System.out.println("NOTIFICATION ID = " + notification.getNotificationId());
                System.out.println("NOTIFICATION USER ID = " +
                        notification.getUser().getUserId());
            } return notifications
                    .stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }
    }@Override
    public List<NotificationResponse> getMyUnreadNotifications() {
        Object principal = getCurrentPrincipal();
        if (principal instanceof Admin) {
            return notificationRepository.findByAdminAndIsReadFalseOrderByCreatedAtDesc((Admin) principal)
                    .stream().map(this::mapToResponse).collect(Collectors.toList());
        } else {
            return notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc((User) principal)
                    .stream().map(this::mapToResponse).collect(Collectors.toList());
        }
    }@Override
    public long getMyUnreadCount() {
        Object principal = getCurrentPrincipal();
        if (principal instanceof Admin) {
            return notificationRepository.countByAdminAndIsReadFalse((Admin) principal);
        } else {
            return notificationRepository.countByUserAndIsReadFalse((User) principal);
        }
    }@Override
    public NotificationResponse markAsRead(UUID notificationId) {
        Object principal = getCurrentPrincipal();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        if (principal instanceof Admin) {
            Admin admin = (Admin) principal;
            if (notification.getAdmin() == null || !notification.getAdmin().getId().equals(admin.getId())) {
                throw new RuntimeException("Unauthorized notification access");
            }
        } else {
            User user = (User) principal;
            if (notification.getUser() == null || !notification.getUser().getUserId().equals(user.getUserId())) {
                throw new RuntimeException("Unauthorized notification access");
            }
        }notification.setIsRead(true);
        Notification saved = notificationRepository.save(notification);
        return mapToResponse(saved);
    }@Override
    public void markAllAsRead() {
        Object principal = getCurrentPrincipal();
        List<Notification> unreadNotifications;
        if (principal instanceof Admin) {
            unreadNotifications = notificationRepository.findByAdminAndIsReadFalse((Admin) principal);
        } else {
            unreadNotifications = notificationRepository.findByUserAndIsReadFalse((User) principal);
        }
        unreadNotifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }@Override
    public Object sendNotification(NotificationSendRequest request) {
        Object principal = getCurrentPrincipal();
        UUID senderId;
        String senderType;
        String senderRoleName;
        if (principal instanceof Admin) {
            senderId = ((Admin) principal).getId();
            senderType = "ADMIN";
            senderRoleName = "ADMIN";
        } else {
            User user = (User) principal;
            senderId = user.getUserId();
            senderRoleName = user.getRole().name();
            senderType = senderRoleName;
        }String receiverType = request.getReceiverType();
        UUID receiverId = request.getReceiverId();
        if (receiverType == null || receiverType.trim().isEmpty()) {
            throw new RuntimeException("receiverType cannot be null or empty");
        }if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Title cannot be empty");
        }if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            throw new RuntimeException("Message cannot be empty");
        }if (request.getNotificationType() == null) {
            throw new RuntimeException("notificationType cannot be null");
        }receiverType = receiverType.toUpperCase();
        if (senderRoleName.equals("CUSTOMER")) {
            if (!receiverType.equals("FARMER") && !receiverType.equals("ADMIN")) {
                throw new RuntimeException("Customers can only send notifications to Farmers or Admins");
            }
        } else if (senderRoleName.equals("FARMER")) {
            if (!receiverType.equals("CUSTOMER") && !receiverType.equals("ADMIN")) {
                throw new RuntimeException("Farmers can only send notifications to Customers or Admins");
            }
        } else if (senderRoleName.equals("ADMIN")) {
            // Admin can send to CUSTOMER or FARMER
            if (!receiverType.equals("CUSTOMER") && !receiverType.equals("FARMER")) {
                throw new RuntimeException("Admins can only send notifications to Customers or Farmers");
            }
        } else {
            throw new RuntimeException("Unauthorized sender type");
        }
        List<User> receivers = new ArrayList<>();
        List<Admin> adminReceivers = new ArrayList<>();

        if (receiverType.equals("ADMIN")) {
            if (receiverId != null) {
                Admin adminReceiver = adminRepository.findById(receiverId)
                        .orElseThrow(() -> new RuntimeException("Receiver not found"));
                adminReceivers.add(adminReceiver);
            } else {
                adminReceivers = adminRepository.findAll(); // Assuming all admins should receive broadcast
            }
            if (adminReceivers == null || adminReceivers.isEmpty()) {
                throw new RuntimeException("No active receivers found");
            }
        } else {
            if (receiverId != null) {
                User receiver = userRepository.findById(receiverId)
                        .orElseThrow(() -> new RuntimeException("Receiver not found"));
                if (!receiver.getRole().name().equals(receiverType)) {
                    throw new RuntimeException("Receiver role mismatch");
                }
                receivers.add(receiver);
            } else {
                // Broadcast
                Role roleEnum;
                try {
                    roleEnum = Role.valueOf(receiverType);
                } catch (Exception e) {
                    throw new RuntimeException("Invalid receiverType");
                }
                receivers = userRepository.findByRoleAndIsActiveTrue(roleEnum);
            }
            if (receivers == null || receivers.isEmpty()) {
                throw new RuntimeException("No active receivers found");
            }
        }

        List<Notification> notifications = new ArrayList<>();
        
        if (receiverType.equals("ADMIN")) {
            for (Admin adminReceiver : adminReceivers) {
                Notification notification = new Notification();
                notification.setAdmin(adminReceiver);
                notification.setSenderId(senderId);
                notification.setSenderType(senderType);
                notification.setTitle(request.getTitle());
                notification.setMessage(request.getMessage());
                notification.setNotificationType(request.getNotificationType());
                notifications.add(notification);
            }
        } else {
            for (User receiver : receivers) {
                System.out.println("Receiver ID (Requested) = " + receiverId);
                System.out.println("Receiver User ID (Actual from DB) = " + receiver.getUserId());
                System.out.println("Receiver Role = " + receiver.getRole());
                Notification notification = new Notification();
                notification.setUser(receiver);
                notification.setSenderId(senderId);
                notification.setSenderType(senderType);
                notification.setTitle(request.getTitle());
                notification.setMessage(request.getMessage());
                notification.setNotificationType(request.getNotificationType());
                notifications.add(notification);
                
                System.out.println("Saving notification for user ID = " + receiver.getUserId());
            }
        }
        List<Notification> saved = notificationRepository.saveAll(notifications);
        if (!saved.isEmpty()) {
            System.out.println("Saved notification ID = " + saved.get(0).getNotificationId());
            if (saved.get(0).getUser() != null) {
                System.out.println("Saved notification user ID = " + saved.get(0).getUser().getUserId());
            }
        }

        // 4. Return response
        if (receiverId != null && !saved.isEmpty()) {
            return mapToResponse(saved.get(0));
        } else {
            return Map.of("sentCount", saved.size());
        }
    }private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .notificationType(notification.getNotificationType())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .senderId(notification.getSenderId())
                .senderType(notification.getSenderType())
                .build();
    }

    @Override
    public List<com.Siddhant.UserApp.dto.NotificationHistoryResponse> getMySentNotifications() {
        Object principal = getCurrentPrincipal();
        if (!(principal instanceof Admin)) {
            throw new RuntimeException("Only Admins can fetch sent message history");
        }
        Admin admin = (Admin) principal;
        List<Notification> notifications = notificationRepository.findBySenderIdOrderByCreatedAtDesc(admin.getId());
        return notifications.stream().map(n -> {
            com.Siddhant.UserApp.dto.NotificationHistoryResponse.NotificationHistoryResponseBuilder builder = 
                com.Siddhant.UserApp.dto.NotificationHistoryResponse.builder()
                    .notificationId(n.getNotificationId())
                    .title(n.getTitle())
                    .message(n.getMessage())
                    .notificationType(n.getNotificationType())
                    .createdAt(n.getCreatedAt())
                    .senderId(n.getSenderId())
                    .senderType(n.getSenderType());
            
            if (n.getUser() != null) {
                builder.receiverId(n.getUser().getUserId());
                builder.receiverName(n.getUser().getName());
                builder.receiverType(n.getUser().getRole().name());
            } else if (n.getAdmin() != null && !n.getAdmin().getId().equals(admin.getId())) {
                builder.receiverId(n.getAdmin().getId());
                builder.receiverName(n.getAdmin().getName());
                builder.receiverType("ADMIN");
            } else {
                // Determine broadcast logic if receiver is null.
                // Assuming it was sent to all based on title/message context if no user is assigned.
                if (n.getMessage().contains("All Farmers")) {
                    builder.receiverName("All Farmers");
                    builder.receiverType("FARMER");
                } else if (n.getMessage().contains("All Customers")) {
                    builder.receiverName("All Customers");
                    builder.receiverType("CUSTOMER");
                } else {
                    // Fallback
                    builder.receiverName("Broadcast");
                    builder.receiverType(n.getNotificationType().name());
                }
            }
            return builder.build();
        }).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public void deleteNotification(UUID notificationId) {
        Object principal = getCurrentPrincipal();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
                
        // Ensure the current user owns the notification (or sent it)
        if (principal instanceof Admin) {
            Admin admin = (Admin) principal;
            boolean isReceiver = notification.getAdmin() != null && notification.getAdmin().getId().equals(admin.getId());
            boolean isSender = notification.getSenderId() != null && notification.getSenderId().equals(admin.getId());
            if (!isReceiver && !isSender) {
                throw new RuntimeException("Unauthorized notification access");
            }
        } else {
            User user = (User) principal;
            boolean isReceiver = notification.getUser() != null && notification.getUser().getUserId().equals(user.getUserId());
            boolean isSender = notification.getSenderId() != null && notification.getSenderId().equals(user.getUserId());
            if (!isReceiver && !isSender) {
                throw new RuntimeException("Unauthorized notification access");
            }
        }
        
        notificationRepository.delete(notification);
    }

    private Object getCurrentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("AUTH NAME = " + authentication.getName());
        System.out.println("AUTHORITIES = " + authentication.getAuthorities());
        System.out.println("AUTHENTICATED = " + authentication.isAuthenticated());
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        String username = authentication.getName();
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return adminRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("Admin not found"));
        } else {

            User user = userRepository.findFirstByEmail(username)
                    .orElseGet(() -> userRepository.findFirstByMobile(username)
                            .orElseThrow(() -> new RuntimeException("User not found")));
            System.out.println("CURRENT USER ID = " + user.getUserId());
            System.out.println("CURRENT USER EMAIL = " + user.getEmail());
            System.out.println("CURRENT USER MOBILE = " + user.getMobile());
            System.out.println("CURRENT USER ROLE = " + user.getRole());
            return user;
        }
    }
}
