package com.Siddhant.UserApp.Service.Impl;

import com.Siddhant.UserApp.Entity.Admin;
import com.Siddhant.UserApp.Entity.SupportMessage;
import com.Siddhant.UserApp.Entity.SupportRequest;
import com.Siddhant.UserApp.Entity.User;
import com.Siddhant.UserApp.Repository.AdminRepository;
import com.Siddhant.UserApp.Repository.SupportMessageRepository;
import com.Siddhant.UserApp.Repository.SupportRequestRepository;
import com.Siddhant.UserApp.Repository.UserRepository;
import com.Siddhant.UserApp.Service.SupportMessageService;
import com.Siddhant.UserApp.Service.NotificationService;
import com.Siddhant.UserApp.dto.NotificationSendRequest;
import com.Siddhant.UserApp.dto.SupportMessageDto;
import com.Siddhant.UserApp.enums.NotificationType;
import com.Siddhant.UserApp.Entity.Role;
import com.Siddhant.UserApp.enums.SupportRequestStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupportMessageServiceImpl implements SupportMessageService {

    private final SupportMessageRepository supportMessageRepository;
    private final SupportRequestRepository supportRequestRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final NotificationService notificationService;

    @Override
    public List<SupportMessageDto> getMessagesForSupportRequest(UUID supportRequestId, String roleStr) {
        boolean isAdmin = "ADMIN".equalsIgnoreCase(roleStr);
        return supportMessageRepository.findBySupportRequest_SupportRequestIdOrderByCreatedAtAsc(supportRequestId)
                .stream()
                .filter(msg -> {
                    if (isAdmin) {
                        return !Boolean.TRUE.equals(msg.getDeletedByAdmin());
                    } else {
                        return !Boolean.TRUE.equals(msg.getDeletedByFarmer());
                    }
                })
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public SupportMessageDto sendMessage(UUID supportRequestId, String message, String username, String roleStr) {
        SupportRequest request = supportRequestRepository.findById(supportRequestId)
                .orElseThrow(() -> new RuntimeException("Support Request not found"));

        UUID senderId = null;
        Role senderType = null;
        String senderName = "";

        if ("ADMIN".equalsIgnoreCase(roleStr)) {
            Admin admin = adminRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("Admin not found"));
            senderId = admin.getId();
            senderType = Role.ADMIN;
            senderName = "Admin";
            
            // If admin replies, we can move it to IN_PROGRESS if it was OPEN
            if (request.getStatus() == SupportRequestStatus.OPEN) {
                request.setStatus(SupportRequestStatus.IN_PROGRESS);
                supportRequestRepository.save(request);
            }
        } else {
            User user = userRepository.findFirstByEmail(username)
                    .orElseGet(() -> userRepository.findFirstByMobile(username)
                            .orElseThrow(() -> new RuntimeException("User not found")));
            
            // Verify ownership if sent by farmer/customer
            if (!request.getUser().getUserId().equals(user.getUserId())) {
                throw new RuntimeException("Not authorized to access this support request");
            }
            senderId = user.getUserId();
            senderType = user.getRole();
            senderName = user.getName() != null ? user.getName() : "User";
        }

        SupportMessage supportMessage = new SupportMessage();
        supportMessage.setSupportRequest(request);
        supportMessage.setMessage(message);
        supportMessage.setSenderId(senderId);
        supportMessage.setSenderType(senderType);
        
        SupportMessage savedMessage = supportMessageRepository.save(supportMessage);

        // Send notification
        if ("ADMIN".equalsIgnoreCase(roleStr)) {
            NotificationSendRequest notifReq = new NotificationSendRequest();
            notifReq.setReceiverType("FARMER");
            notifReq.setReceiverId(request.getUser().getUserId());
            notifReq.setTitle("Admin Support Reply");
            
            // Truncate message if it's too long for a notification
            String previewMessage = message.length() > 200 ? message.substring(0, 197) + "..." : message;
            notifReq.setMessage("Admin: " + previewMessage);
            notifReq.setNotificationType(NotificationType.ADMIN);
            
            // Commenting out generic notification because support chat has its own isolated unread count logic
            // notificationService.sendNotification(notifReq);
        } else {
            NotificationSendRequest notifReq = new NotificationSendRequest();
            notifReq.setReceiverType("ADMIN");
            notifReq.setReceiverId(null);
            notifReq.setTitle("Message from " + senderName);
            
            String previewMessage = message.length() > 200 ? message.substring(0, 197) + "..." : message;
            notifReq.setMessage(senderName + ": " + previewMessage);
            notifReq.setNotificationType(NotificationType.ADMIN);
            // Commenting out generic notification because support chat has its own isolated unread count logic
            // notificationService.sendNotification(notifReq);
        }

        return mapToDto(savedMessage);
    }
    @Override
    public long getUnreadCountForFarmer(UUID farmerId) {
        return supportMessageRepository.countBySupportRequest_User_UserIdAndSenderTypeAndIsReadFalse(farmerId, Role.ADMIN);
    }

    @Override
    public java.util.Map<UUID, Long> getUnreadCountsForAdmin() {
        List<Object[]> results = supportMessageRepository.countUnreadMessagesGroupedByFarmerProfile(Role.FARMER);
        java.util.Map<UUID, Long> counts = new java.util.HashMap<>();
        for (Object[] result : results) {
            UUID farmerProfileId = (UUID) result[0];
            Long count = (Long) result[1];
            counts.put(farmerProfileId, count);
        }
        return counts;
    }

    @Override
    public void markMessagesAsRead(UUID supportRequestId, String roleStr) {
        if ("ADMIN".equalsIgnoreCase(roleStr)) {
            supportMessageRepository.markMessagesAsRead(supportRequestId, Role.FARMER);
        } else {
            supportMessageRepository.markMessagesAsRead(supportRequestId, Role.ADMIN);
        }
    }
    
    @Override
    public void deleteMessage(UUID messageId, String roleStr) {
        java.util.Optional<SupportMessage> messageOpt = supportMessageRepository.findById(messageId);
        if (messageOpt.isPresent()) {
            SupportMessage message = messageOpt.get();
            if ("ADMIN".equalsIgnoreCase(roleStr)) {
                message.setDeletedByAdmin(true);
            } else {
                message.setDeletedByFarmer(true);
            }
            supportMessageRepository.save(message);
        }
    }

    private SupportMessageDto mapToDto(SupportMessage message) {
        return SupportMessageDto.builder()
                .supportMessageId(message.getSupportMessageId())
                .supportRequestId(message.getSupportRequest().getSupportRequestId())
                .senderId(message.getSenderId())
                .senderType(message.getSenderType())
                .message(message.getMessage())
                .isRead(message.getIsRead())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
