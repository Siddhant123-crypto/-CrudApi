package com.Siddhant.UserApp.Controller;
import com.Siddhant.UserApp.Entity.AdminGroupMessage;
import com.Siddhant.UserApp.Service.AdminGroupMessageService;
import com.Siddhant.UserApp.dto.AdminGroupMessageRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/admin/group")
public class AdminGroupMessageController {
    private final AdminGroupMessageService adminGroupMessageService;
    public AdminGroupMessageController(AdminGroupMessageService adminGroupMessageService) {
        this.adminGroupMessageService = adminGroupMessageService;
    }@PostMapping("/messages")
    public ResponseEntity<AdminGroupMessage> sendMessage(
            @Valid @RequestBody AdminGroupMessageRequest request) {return ResponseEntity.ok(
                adminGroupMessageService.sendMessage(request.getMessage()));
    }@GetMapping("/messages")
    public ResponseEntity<List<AdminGroupMessage>> getMessages() {return ResponseEntity.ok(
                adminGroupMessageService.getMessages());
    }@DeleteMapping("/messages/{messageId}")
    public ResponseEntity<java.util.Map<String, Object>> deleteMessage(@PathVariable java.util.UUID messageId) {
        adminGroupMessageService.deleteMessage(messageId);java.util.Map<String, Object> response = new java.util.LinkedHashMap<>();response.put("message", "Message deleted successfully");response.put("data", null);
        return ResponseEntity.ok(response);
    }
}