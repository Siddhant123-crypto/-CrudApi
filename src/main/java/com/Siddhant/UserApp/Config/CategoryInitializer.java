package com.Siddhant.UserApp.Config;
import com.Siddhant.UserApp.Entity.Category;
import com.Siddhant.UserApp.Repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
@Configuration
public class CategoryInitializer {
    @Bean
    public CommandLineRunner initCategories(CategoryRepository categoryRepository) {
        return args -> {
            List<Category> defaults = Arrays.asList(
                    new Category(null, "Vegetable", "Fresh vegetables", true, null, null),
                    new Category(null, "Root Vegetable", "Potatoes, carrots, etc.", true, null, null),
                    new Category(null, "Fruit", "Fresh farm fruits", true, null, null),
                    new Category(null, "Grains", "Healthy grains", true, null, null),
                    new Category(null, "Dairy", "Milk & dairy products", true, null, null),
                    new Category(null, "Wild Forest Vegetable", "Rare forest produce", true, null, null)
            );for (Category cat : defaults) {
                if (!categoryRepository.existsByNameIgnoreCase(cat.getName())) {categoryRepository.save(cat);
                }
            }categoryRepository.findAll().forEach(cat -> {
                if (cat.getName().equalsIgnoreCase("Fruits")) {
                    categoryRepository.delete(cat);
                }
            });
        };
    }
}
