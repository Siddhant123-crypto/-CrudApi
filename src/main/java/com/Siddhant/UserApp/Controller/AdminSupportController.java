package com.Siddhant.UserApp.Controller;
import com.Siddhant.UserApp.Service.SupportRequestService;
import com.Siddhant.UserApp.dto.SupportRequestResponse;
import com.Siddhant.UserApp.dto.SupportStatusUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Siddhant.UserApp.Service.SupportMessageService;
import com.Siddhant.UserApp.dto.SupportMessageDto;
import com.Siddhant.UserApp.dto.SendSupportMessageRequest;
import com.Siddhant.UserApp.enums.SupportRequestStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;
@RestController
@RequestMapping("/admin/support")
@RequiredArgsConstructor
public class AdminSupportController {
    private final SupportRequestService supportRequestService;
    private final SupportMessageService supportMessageService;
    private final com.Siddhant.UserApp.Repository.FarmerProfileRepository farmerProfileRepository;
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSupportRequests() {
        List<SupportRequestResponse> requests = supportRequestService.getAllSupportRequests();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "All support requests fetched successfully");
        response.put("data", requests);
        return ResponseEntity.ok(response);
    }@GetMapping("/{supportRequestId}")
    public ResponseEntity<Map<String, Object>> getSupportRequestById(@PathVariable UUID supportRequestId) {
        SupportRequestResponse request = supportRequestService.getSupportRequestById(supportRequestId);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Support request fetched successfully");
        response.put("data", request);
        return ResponseEntity.ok(response);
    }@PutMapping("/{supportRequestId}/status")
    public ResponseEntity<Map<String, Object>> updateSupportRequestStatus(
            @PathVariable UUID supportRequestId,
            @Valid @RequestBody SupportStatusUpdateRequest request) {
        SupportRequestResponse updated = supportRequestService.updateSupportRequestStatus(supportRequestId, request.getStatus());
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Support request status updated successfully");
        response.put("data", updated);
        return ResponseEntity.ok(response);
    }@GetMapping("/farmer/{farmerId}/active")
    public ResponseEntity<Map<String, Object>> getActiveSupportRequestForFarmer(@PathVariable UUID farmerId) {
        UUID userId = farmerProfileRepository.findById(farmerId)
                .map(f -> f.getUser().getUserId())
                .orElse(farmerId);
        List<SupportRequestResponse> allRequests = supportRequestService.getAllSupportRequests();
        Optional<SupportRequestResponse> activeRequest = allRequests.stream()
                .filter(r -> r.getUserId() != null && r.getUserId().equals(userId) 
                        && (SupportRequestStatus.OPEN.equals(r.getStatus()) || SupportRequestStatus.IN_PROGRESS.equals(r.getStatus())))
                .max((r1, r2) -> {
                    if (r1.getCreatedAt() == null && r2.getCreatedAt() == null) return 0;
                    if (r1.getCreatedAt() == null) return -1;
                    if (r2.getCreatedAt() == null) return 1;
                    return r1.getCreatedAt().compareTo(r2.getCreatedAt());
                });
        Map<String, Object> response = new LinkedHashMap<>();
        if (activeRequest.isPresent()) {
            response.put("message", "Active support request found");
            response.put("data", activeRequest.get());
        } else {
            response.put("message", "No active support request found");
            response.put("data", null);
        }return ResponseEntity.ok(response);
    }@GetMapping("/{supportRequestId}/messages")
    public ResponseEntity<Map<String, Object>> getMessages(@PathVariable UUID supportRequestId) {
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        List<SupportMessageDto> messages = supportMessageService.getMessagesForSupportRequest(supportRequestId, role);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Messages fetched successfully");
        response.put("data", messages);
        return ResponseEntity.ok(response);
    }@PostMapping("/{supportRequestId}/messages")
    public ResponseEntity<Map<String, Object>> sendMessage(@PathVariable UUID supportRequestId, @Valid @RequestBody SendSupportMessageRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        SupportMessageDto sent = supportMessageService.sendMessage(supportRequestId, request.getMessage(), username, role);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Message sent successfully");
        response.put("data", sent);
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
