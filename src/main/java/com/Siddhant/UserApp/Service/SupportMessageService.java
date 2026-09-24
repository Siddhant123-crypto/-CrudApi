package com.Siddhant.UserApp.Service;

import com.Siddhant.UserApp.dto.SupportMessageDto;
import java.util.List;
import java.util.UUID;

public interface SupportMessageService {
    List<SupportMessageDto> getMessagesForSupportRequest(UUID supportRequestId, String roleStr);
    SupportMessageDto sendMessage(UUID supportRequestId, String message, String username, String role);

    long getUnreadCountForFarmer(UUID farmerId);
    java.util.Map<UUID, Long> getUnreadCountsForAdmin();

    void markMessagesAsRead(UUID supportRequestId, String role);
    void deleteMessage(UUID messageId, String roleStr);
}
