package com.Siddhant.UserApp.Controller;

import com.Siddhant.UserApp.Service.SupportRequestService;
import com.Siddhant.UserApp.dto.SupportRequestRequest;
import com.Siddhant.UserApp.dto.SupportRequestResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/support")
@RequiredArgsConstructor
public class SupportRequestController {

    private final SupportRequestService supportRequestService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createSupportRequest(@Valid @RequestBody SupportRequestRequest request) {
        SupportRequestResponse created = supportRequestService.createSupportRequest(request);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Support request created successfully");
        response.put("data", created);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMySupportRequests() {
        List<SupportRequestResponse> requests = supportRequestService.getMySupportRequests();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Support requests fetched successfully");
        response.put("data", requests);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{supportRequestId}")
    public ResponseEntity<Map<String, Object>> getMySupportRequestById(@PathVariable UUID supportRequestId) {
        SupportRequestResponse request = supportRequestService.getMySupportRequestById(supportRequestId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Support request fetched successfully");
        response.put("data", request);

        return ResponseEntity.ok(response);
    }
}
