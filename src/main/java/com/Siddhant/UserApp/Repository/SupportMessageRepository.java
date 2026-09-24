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
    @Query("UPDATE SupportMessage s SET s.isRead = true WHERE s.supportRequest.supportRequestId = :requestId AND s.senderType = :senderType AND s.isRead = false")
    int markMessagesAsRead(@Param("requestId") UUID requestId, @Param("senderType") Role senderType);

    @Query("SELECT fp.id, COUNT(s) FROM SupportMessage s JOIN FarmerProfile fp ON s.supportRequest.user.userId = fp.user.userId WHERE s.senderType = :senderType AND s.isRead = false GROUP BY fp.id")
    List<Object[]> countUnreadMessagesGroupedByFarmerProfile(@Param("senderType") Role senderType);
}
