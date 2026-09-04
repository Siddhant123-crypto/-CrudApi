package com.Siddhant.UserApp.Controller;
import com.Siddhant.UserApp.Service.AdminDashboardService;
import com.Siddhant.UserApp.dto.admin.AdminDashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.LinkedHashMap;
import java.util.Map;
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {
    private final AdminDashboardService adminDashboardService;
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        AdminDashboardResponse dashboard = adminDashboardService.getDashboardSummary();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Admin dashboard fetched successfully");
        response.put("data", dashboard);
        return ResponseEntity.ok(response);
    }
}