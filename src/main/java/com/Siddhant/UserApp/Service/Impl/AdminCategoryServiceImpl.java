package com.Siddhant.UserApp.Service.Impl;
import com.Siddhant.UserApp.Entity.Category;
import com.Siddhant.UserApp.Repository.CategoryRepository;
import com.Siddhant.UserApp.Service.AdminCategoryService;
import com.Siddhant.UserApp.dto.admin.AdminCategoryRequest;
import com.Siddhant.UserApp.dto.admin.AdminCategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private final CategoryRepository categoryRepository;
    @Override
    public AdminCategoryResponse createCategory(AdminCategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName().trim())) {throw new RuntimeException("Category already exists");
        }Category category = new Category();
        category.setName(request.getName().trim());
        category.setDescription(request.getDescription());
        category.setIsActive(true);
        Category savedCategory = categoryRepository.save(category);
        return new AdminCategoryResponse(savedCategory.getCategoryId(), savedCategory.getName(), savedCategory.getDescription(), savedCategory.getIsActive(), savedCategory.getCreatedOn(), savedCategory.getUpdatedOn());
    }@Override
    public List<AdminCategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream().map(category -> new AdminCategoryResponse(category.getCategoryId(),category.getName(),category.getDescription(),category.getIsActive(),category.getCreatedOn(),category.getUpdatedOn()
                )).toList();
    }@Override
    public AdminCategoryResponse getCategoryById(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found"));
        return new AdminCategoryResponse(category.getCategoryId(), category.getName(), category.getDescription(), category.getIsActive(), category.getCreatedOn(), category.getUpdatedOn());
    }@Override
    public AdminCategoryResponse updateCategory(UUID categoryId, AdminCategoryRequest request) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found"));
        if (!category.getName().equalsIgnoreCase(request.getName().trim()) && categoryRepository.existsByNameIgnoreCase(request.getName().trim())) {throw new RuntimeException("Category already exists");
        }category.setName(request.getName().trim());category.setDescription(request.getDescription());Category updatedCategory = categoryRepository.save(category);
        return new AdminCategoryResponse(updatedCategory.getCategoryId(), updatedCategory.getName(), updatedCategory.getDescription(), updatedCategory.getIsActive(), updatedCategory.getCreatedOn(), updatedCategory.getUpdatedOn());
    }@Override
    public AdminCategoryResponse deleteCategory(UUID categoryId) {Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found"));
        AdminCategoryResponse response = new AdminCategoryResponse(category.getCategoryId(), category.getName(), category.getDescription(), category.getIsActive(), category.getCreatedOn(), category.getUpdatedOn()
        );categoryRepository.delete(category);
        return response;
    }@Override
    public AdminCategoryResponse activateCategory(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found"));
        category.setIsActive(true);Category savedCategory = categoryRepository.save(category);
        return new AdminCategoryResponse(savedCategory.getCategoryId(), savedCategory.getName(), savedCategory.getDescription(), savedCategory.getIsActive(), savedCategory.getCreatedOn(), savedCategory.getUpdatedOn());
    }@Override
    public AdminCategoryResponse deactivateCategory(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found"));
        category.setIsActive(false);
        Category savedCategory = categoryRepository.save(category);
        return new AdminCategoryResponse(savedCategory.getCategoryId(), savedCategory.getName(), savedCategory.getDescription(), savedCategory.getIsActive(), savedCategory.getCreatedOn(), savedCategory.getUpdatedOn());
    }
}