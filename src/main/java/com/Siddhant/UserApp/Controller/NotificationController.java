package com.Siddhant.UserApp.Controller;
import com.Siddhant.UserApp.Service.NotificationService;
import com.Siddhant.UserApp.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMyNotifications() {
        List<NotificationResponse> notifications = notificationService.getMyNotifications();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Notifications fetched successfully");
        response.put("data", notifications);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/unread")
    public ResponseEntity<Map<String, Object>> getMyUnreadNotifications() {
        List<NotificationResponse> notifications = notificationService.getMyUnreadNotifications();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Unread notifications fetched successfully");
        response.put("data", notifications);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sent")
    public ResponseEntity<Map<String, Object>> getMySentNotifications() {
        List<com.Siddhant.UserApp.dto.NotificationHistoryResponse> notifications = notificationService.getMySentNotifications();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Sent notifications fetched successfully");
        response.put("data", notifications);
        return ResponseEntity.ok(response);
    }@GetMapping("/unread/count")
    public ResponseEntity<Map<String, Object>> getMyUnreadCount() {
        long count = notificationService.getMyUnreadCount();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("count", count);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Unread notification count fetched successfully");
        response.put("data", data);
        return ResponseEntity.ok(response);
    }@PutMapping("/{notificationId}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable UUID notificationId) {
        NotificationResponse notification = notificationService.markAsRead(notificationId);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Notification marked as read");
        response.put("data", notification);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead() {
        notificationService.markAllAsRead();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "All notifications marked as read");
        response.put("data", null);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Map<String, Object>> deleteNotification(@PathVariable UUID notificationId) {
        notificationService.deleteNotification(notificationId);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Notification deleted successfully");
        response.put("data", null);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendNotification(@RequestBody com.Siddhant.UserApp.dto.NotificationSendRequest requestData) {
        Object result = notificationService.sendNotification(requestData);
        
        Map<String, Object> response = new LinkedHashMap<>();
        if (requestData.getReceiverId() == null) {
            response.put("message", "Notification sent successfully to all " + requestData.getReceiverType().toLowerCase() + "s");
        } else {
            response.put("message", "Notification sent successfully");
        }
        response.put("data", result);
        
        return ResponseEntity.ok(response);
    }
}
