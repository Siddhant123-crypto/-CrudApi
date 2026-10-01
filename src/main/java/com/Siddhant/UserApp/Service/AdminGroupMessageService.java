package com.Siddhant.UserApp.Service;
import com.Siddhant.UserApp.Entity.AdminGroupMessage;
import java.util.List;
import java.util.UUID;

public interface AdminGroupMessageService {
    AdminGroupMessage sendMessage(String message);
    List<AdminGroupMessage> getMessages();
    void deleteMessage(UUID messageId);
}