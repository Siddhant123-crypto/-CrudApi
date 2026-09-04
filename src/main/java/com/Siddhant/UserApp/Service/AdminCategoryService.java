package com.Siddhant.UserApp.Service;
import com.Siddhant.UserApp.dto.admin.AdminCategoryRequest;
import com.Siddhant.UserApp.dto.admin.AdminCategoryResponse;

import java.util.List;
import java.util.UUID;

public interface AdminCategoryService {
    AdminCategoryResponse createCategory(AdminCategoryRequest request);
    List<AdminCategoryResponse> getAllCategories();
    AdminCategoryResponse getCategoryById(UUID categoryId);
    AdminCategoryResponse updateCategory(UUID categoryId, AdminCategoryRequest request);
    AdminCategoryResponse deleteCategory(UUID categoryId);
    AdminCategoryResponse activateCategory(UUID categoryId);
    AdminCategoryResponse deactivateCategory(UUID categoryId);
}