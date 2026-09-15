package com.Siddhant.UserApp.Controller;

import com.Siddhant.UserApp.Service.SupportRequestService;
import com.Siddhant.UserApp.dto.SupportRequestResponse;
import com.Siddhant.UserApp.dto.SupportStatusUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin/support")
@RequiredArgsConstructor
public class AdminSupportController {

    private final SupportRequestService supportRequestService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSupportRequests() {
        List<SupportRequestResponse> requests = supportRequestService.getAllSupportRequests();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "All support requests fetched successfully");
        response.put("data", requests);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{supportRequestId}")
    public ResponseEntity<Map<String, Object>> getSupportRequestById(@PathVariable UUID supportRequestId) {
        SupportRequestResponse request = supportRequestService.getSupportRequestById(supportRequestId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Support request fetched successfully");
        response.put("data", request);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{supportRequestId}/status")
    public ResponseEntity<Map<String, Object>> updateSupportRequestStatus(
            @PathVariable UUID supportRequestId,
            @Valid @RequestBody SupportStatusUpdateRequest request) {
        
        SupportRequestResponse updated = supportRequestService.updateSupportRequestStatus(supportRequestId, request.getStatus());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Support request status updated successfully");
        response.put("data", updated);

        return ResponseEntity.ok(response);
    }
}
