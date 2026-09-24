package com.Siddhant.UserApp.Controller;
import com.Siddhant.UserApp.Service.SupportRequestService;
import com.Siddhant.UserApp.dto.SupportRequestRequest;
import com.Siddhant.UserApp.dto.SupportRequestResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.Siddhant.UserApp.Service.SupportMessageService;
import com.Siddhant.UserApp.dto.SupportMessageDto;
import com.Siddhant.UserApp.dto.SendSupportMessageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@RestController
@RequestMapping("/support")
@RequiredArgsConstructor
public class SupportRequestController {
    private final SupportRequestService supportRequestService;
    private final SupportMessageService supportMessageService;
    private final com.Siddhant.UserApp.Repository.UserRepository userRepository;
    @PostMapping
    public ResponseEntity<Map<String, Object>> createSupportRequest(@Valid @RequestBody SupportRequestRequest request) {
        SupportRequestResponse created = supportRequestService.createSupportRequest(request);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Support request created successfully");response.put("data", created);
        return ResponseEntity.ok(response);
    }@GetMapping
    public ResponseEntity<Map<String, Object>> getMySupportRequests() {
        List<SupportRequestResponse> requests = supportRequestService.getMySupportRequests();
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Support requests fetched successfully");response.put("data", requests);
        return ResponseEntity.ok(response);
    }@GetMapping("/{supportRequestId}")
    public ResponseEntity<Map<String, Object>> getMySupportRequestById(@PathVariable UUID supportRequestId) {
        SupportRequestResponse request = supportRequestService.getMySupportRequestById(supportRequestId);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Support request fetched successfully");response.put("data", request);
        return ResponseEntity.ok(response);
    }@GetMapping("/{supportRequestId}/messages")
    public ResponseEntity<Map<String, Object>> getMessages(@PathVariable UUID supportRequestId) {
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        List<SupportMessageDto> messages = supportMessageService.getMessagesForSupportRequest(supportRequestId, role);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Messages fetched successfully");response.put("data", messages);
        return ResponseEntity.ok(response);
    }@PostMapping("/{supportRequestId}/messages")
    public ResponseEntity<Map<String, Object>> sendMessage(@PathVariable UUID supportRequestId, @Valid @RequestBody SendSupportMessageRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        SupportMessageDto sent = supportMessageService.sendMessage(supportRequestId, request.getMessage(), username, role);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Message sent successfully");response.put("data", sent);
        return ResponseEntity.ok(response);
    }@GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        com.Siddhant.UserApp.Entity.User user = userRepository.findFirstByEmail(username)
                .orElseGet(() -> userRepository.findFirstByMobile(username).orElseThrow(() -> new RuntimeException("User not found")));
        long count = supportMessageService.getUnreadCountForFarmer(user.getUserId());
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Unread count fetched successfully");response.put("data", count);
        return ResponseEntity.ok(response);
    }@GetMapping("/admin/unread-counts")
    public ResponseEntity<Map<String, Object>> getUnreadCountsForAdmin() {
        Map<UUID, Long> counts = supportMessageService.getUnreadCountsForAdmin();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Admin unread counts fetched successfully");
        response.put("data", counts);
        return ResponseEntity.ok(response);
    }@PutMapping("/{supportRequestId}/messages/read")
    public ResponseEntity<Map<String, Object>> markMessagesAsRead(@PathVariable UUID supportRequestId) {
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        supportMessageService.markMessagesAsRead(supportRequestId, role);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Messages marked as read");
        return ResponseEntity.ok(response);
    }@DeleteMapping("/{supportRequestId}/messages/{messageId}")
    public ResponseEntity<Map<String, Object>> deleteMessage(@PathVariable UUID supportRequestId, @PathVariable UUID messageId) {
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        supportMessageService.deleteMessage(messageId, role);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Message deleted successfully");
        return ResponseEntity.ok(response);
    }
}
