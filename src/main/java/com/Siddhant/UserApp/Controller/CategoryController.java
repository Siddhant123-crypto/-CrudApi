package com.Siddhant.UserApp.Controller;
import com.Siddhant.UserApp.Service.AdminCategoryService;
import com.Siddhant.UserApp.dto.admin.AdminCategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final AdminCategoryService adminCategoryService;
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCategories() {
        List<AdminCategoryResponse> categories = adminCategoryService.getAllCategories();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Categories fetched successfully");
        response.put("data", categories);
        return ResponseEntity.ok(response);
    }
}
