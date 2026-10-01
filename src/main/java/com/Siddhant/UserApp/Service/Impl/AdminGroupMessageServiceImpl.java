package com.Siddhant.UserApp.Service.Impl;
import com.Siddhant.UserApp.Entity.Admin;
import com.Siddhant.UserApp.Entity.AdminGroupMessage;
import com.Siddhant.UserApp.Entity.AdminGroupMessageDeletion;
import com.Siddhant.UserApp.Repository.AdminGroupMessageDeletionRepository;
import com.Siddhant.UserApp.Repository.AdminGroupMessageRepository;
import com.Siddhant.UserApp.Repository.AdminRepository;
import com.Siddhant.UserApp.Service.AdminGroupMessageService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
public class AdminGroupMessageServiceImpl implements AdminGroupMessageService {
    private final AdminGroupMessageRepository adminGroupMessageRepository;
    private final AdminRepository adminRepository;
    private final AdminGroupMessageDeletionRepository deletionRepository;
    public AdminGroupMessageServiceImpl(AdminGroupMessageRepository adminGroupMessageRepository, AdminRepository adminRepository, AdminGroupMessageDeletionRepository deletionRepository) {
        this.adminGroupMessageRepository = adminGroupMessageRepository;
        this.adminRepository = adminRepository;
        this.deletionRepository = deletionRepository;
    }@Override
    public AdminGroupMessage sendMessage(String message) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin admin = adminRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Admin not found"));
        AdminGroupMessage adminGroupMessage = new AdminGroupMessage();
        adminGroupMessage.setSenderAdminId(admin.getId());
        adminGroupMessage.setMessage(message);
        adminGroupMessage.setCreatedAt(LocalDateTime.now());
        adminGroupMessage.setIsDeleted(false);
        return adminGroupMessageRepository.save(adminGroupMessage);
    }@Override
    public List<AdminGroupMessage> getMessages() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin admin = adminRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Admin not found"));
        
        List<AdminGroupMessageDeletion> deletions = deletionRepository.findByAdminId(admin.getId());
        List<UUID> deletedMessageIds = deletions.stream().map(AdminGroupMessageDeletion::getMessageId).collect(Collectors.toList());
        
        return adminGroupMessageRepository.findAllByOrderByCreatedAtAsc().stream()
                .filter(m -> m.getIsDeleted() == null || !m.getIsDeleted())
                .filter(m -> !deletedMessageIds.contains(m.getId()))
                .collect(Collectors.toList());
    }@Override
    public void deleteMessage(UUID messageId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Admin admin = adminRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Admin not found"));
        AdminGroupMessage message = adminGroupMessageRepository.findById(messageId).orElseThrow(() -> new RuntimeException("Message not found"));
        
        if (message.getSenderAdminId().equals(admin.getId())) {
            message.setIsDeleted(true);
            adminGroupMessageRepository.save(message);
        } else {
            if (!deletionRepository.existsByMessageIdAndAdminId(messageId, admin.getId())) {
                AdminGroupMessageDeletion deletion = new AdminGroupMessageDeletion();
                deletion.setMessageId(messageId);
                deletion.setAdminId(admin.getId());
                deletion.setDeletedAt(LocalDateTime.now());
                deletionRepository.save(deletion);
            }
        }
    }
}