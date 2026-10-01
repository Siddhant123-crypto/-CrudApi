package com.Siddhant.UserApp.Repository;
import com.Siddhant.UserApp.Entity.SupportMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import com.Siddhant.UserApp.Entity.Role;
import jakarta.transaction.Transactional;
@Repository
public interface SupportMessageRepository extends JpaRepository<SupportMessage, UUID> {
    List<SupportMessage> findBySupportRequest_SupportRequestIdOrderByCreatedAtAsc(UUID supportRequestId);
    long countBySupportRequest_User_UserIdAndSenderTypeAndIsReadFalse(UUID userId, Role senderType);
    @Modifying
    @Transactional
    @Query("UPDATE SupportMessage s SET s.isRead = true WHERE s.supportRequest.supportRequestId = :requestId AND s.senderType != com.Siddhant.UserApp.Entity.Role.ADMIN AND s.isRead = false")
    int markMessagesAsRead(@Param("requestId") UUID requestId);
    @Modifying
    @Transactional
    @Query("UPDATE SupportMessage s SET s.isRead = true WHERE s.supportRequest.supportRequestId = :requestId AND s.senderType = com.Siddhant.UserApp.Entity.Role.ADMIN AND s.isRead = false")
    int markAdminMessagesAsRead(@Param("requestId") UUID requestId);
    @Query("SELECT s.supportRequest.user.userId, COUNT(s) FROM SupportMessage s WHERE s.senderType != com.Siddhant.UserApp.Entity.Role.ADMIN AND s.isRead = false GROUP BY s.supportRequest.user.userId")
    List<Object[]> countUnreadMessagesGroupedByUserId();
    @Query("SELECT COUNT(s) FROM SupportMessage s WHERE s.senderType != com.Siddhant.UserApp.Entity.Role.ADMIN AND s.isRead = false AND s.supportRequest.assignedAdminId IS NULL")
    long countUnassignedUnreadMessages();
    @Query("SELECT COUNT(s) FROM SupportMessage s WHERE s.senderType != com.Siddhant.UserApp.Entity.Role.ADMIN AND s.isRead = false AND s.supportRequest.assignedAdminId = :adminId")
    long countMyAssignedUnreadMessages(@Param("adminId") UUID adminId);
    @Query("SELECT COUNT(s) FROM SupportMessage s WHERE s.senderType != com.Siddhant.UserApp.Entity.Role.ADMIN AND s.isRead = false AND s.supportRequest.user.status = 'INACTIVE' AND s.supportRequest.user.blockedUntil IS NOT NULL")
    long countBlockedUnreadMessages();
}
