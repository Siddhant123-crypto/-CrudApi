package com.Siddhant.UserApp.Controller;
import com.Siddhant.UserApp.Service.AdminCategoryService;
import com.Siddhant.UserApp.dto.admin.AdminCategoryRequest;
import com.Siddhant.UserApp.dto.admin.AdminCategoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final AdminCategoryService adminCategoryService;
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCategory(@Valid @RequestBody AdminCategoryRequest request) {
        AdminCategoryResponse category = adminCategoryService.createCategory(request);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Category created successfully");response.put("data", category);
        return ResponseEntity.ok(response);
    }@GetMapping
    public ResponseEntity<Map<String, Object>> getAllCategories() {
        List<AdminCategoryResponse> categories = adminCategoryService.getAllCategories();
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Categories fetched successfully");response.put("data", categories);
        return ResponseEntity.ok(response);
    }@GetMapping("/{categoryId}")
    public ResponseEntity<Map<String, Object>> getCategoryById(@PathVariable UUID categoryId) {
        AdminCategoryResponse category = adminCategoryService.getCategoryById(categoryId);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Category details fetched successfully");response.put("data", category);
        return ResponseEntity.ok(response);
    }@PutMapping("/{categoryId}")
    public ResponseEntity<Map<String, Object>> updateCategory(@PathVariable UUID categoryId, @Valid @RequestBody AdminCategoryRequest request) {
        AdminCategoryResponse category = adminCategoryService.updateCategory(categoryId, request);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Category updated successfully");response.put("data", category);
        return ResponseEntity.ok(response);
    }@DeleteMapping("/{categoryId}")
    public ResponseEntity<Map<String, Object>> deleteCategory(@PathVariable UUID categoryId) {
        AdminCategoryResponse category = adminCategoryService.deleteCategory(categoryId);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Category deleted successfully");response.put("data", category);
        return ResponseEntity.ok(response);
    }@PutMapping("/{categoryId}/activate")
    public ResponseEntity<Map<String, Object>> activateCategory(@PathVariable UUID categoryId) {
        AdminCategoryResponse category = adminCategoryService.activateCategory(categoryId);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Category activated successfully");response.put("data", category);return ResponseEntity.ok(response);
    }@PutMapping("/{categoryId}/deactivate")
    public ResponseEntity<Map<String, Object>> deactivateCategory(@PathVariable UUID categoryId) {
        AdminCategoryResponse category = adminCategoryService.deactivateCategory(categoryId);
        Map<String, Object> response = new LinkedHashMap<>();response.put("message", "Category deactivated successfully");response.put("data", category);
        return ResponseEntity.ok(response);
    }
}