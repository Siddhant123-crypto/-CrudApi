package com.Siddhant.UserApp.Service.Impl;
import com.Siddhant.UserApp.Entity.FarmerProfile;
import com.Siddhant.UserApp.Entity.Product;
import com.Siddhant.UserApp.Repository.FarmerProfileRepository;
import com.Siddhant.UserApp.Repository.ProductRepository;
import com.Siddhant.UserApp.Service.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import com.Siddhant.UserApp.dto.ProductRequest;
import com.Siddhant.UserApp.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.io.File;
import java.io.IOException;


@Service
public class ProductServiceImpl implements ProductService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FarmerProfileRepository farmerProfileRepository;

    @Override
    public ProductResponse saveProduct(ProductRequest request) {

        FarmerProfile farmer = farmerProfileRepository.findById(request.getFarmerId())
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        Product product = new Product();

        product.setFarmer(farmer);
        product.setProductName(request.getProductName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setUnit(request.getUnit());
        product.setDescription(request.getDescription());
        product.setProductPhoto(request.getProductPhoto());

        product.setCreatedBy(farmer.getName());
        product.setCreatedOn(LocalDateTime.now());

        product.setUpdatedBy(farmer.getName());
        product.setUpdatedOn(LocalDateTime.now());

        product.setIsActive(true);
        product.setIsDelete(false);
        product.setStatus("ACTIVE");

        Product savedProduct = productRepository.save(product);

        ProductResponse response = new ProductResponse();

        response.setProductId(savedProduct.getProductId());

        response.setFarmerId(savedProduct.getFarmer().getId());
        response.setFarmerName(savedProduct.getFarmer().getName());
        response.setFarmerAddress(savedProduct.getFarmer().getAddress());

        response.setProductName(savedProduct.getProductName());
        response.setCategory(savedProduct.getCategory());

        response.setPrice(savedProduct.getPrice());
        response.setUnit(savedProduct.getUnit());

        response.setDescription(savedProduct.getDescription());
        response.setProductPhoto(savedProduct.getProductPhoto());

        response.setCreatedBy(savedProduct.getCreatedBy());
        response.setCreatedOn(savedProduct.getCreatedOn());

        response.setUpdatedBy(savedProduct.getUpdatedBy());
        response.setUpdatedOn(savedProduct.getUpdatedOn());

        response.setIsActive(savedProduct.getIsActive());
        response.setIsDelete(savedProduct.getIsDelete());
        response.setStatus(savedProduct.getStatus());

        return response;
    }

    @Override
    public List<ProductResponse> getAllProducts() {

        List<Product> products = productRepository.findAll();
        System.out.println("Total Products : " + products.size());

        List<ProductResponse> responseList = new ArrayList<>();

        for (Product product : products) {

            if (Boolean.TRUE.equals(product.getIsDelete())) {
                continue;
            }

            ProductResponse response = new ProductResponse();

            response.setProductId(product.getProductId());

            response.setFarmerId(product.getFarmer().getId());   // Agar field farmerId hai to getFarmerId() use karo.
            response.setFarmerName(product.getFarmer().getName());
            response.setFarmerAddress(product.getFarmer().getAddress());

            response.setProductName(product.getProductName());
            response.setCategory(product.getCategory());

            response.setPrice(product.getPrice());
            response.setUnit(product.getUnit());

            response.setDescription(product.getDescription());
            response.setProductPhoto(product.getProductPhoto());

            response.setCreatedBy(product.getCreatedBy());
            response.setCreatedOn(product.getCreatedOn());

            response.setUpdatedBy(product.getUpdatedBy());
            response.setUpdatedOn(product.getUpdatedOn());

            response.setIsActive(product.getIsActive());
            response.setIsDelete(product.getIsDelete());
            response.setStatus(product.getStatus());

            responseList.add(response);
        }

        return responseList;
    }

    @Override
    public ProductResponse getProductById(UUID id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductResponse response = new ProductResponse();

        response.setProductId(product.getProductId());

        response.setFarmerId(product.getFarmer().getId()); // Agar field farmerId hai to getFarmerId() use karo.
        response.setFarmerName(product.getFarmer().getName());
        response.setFarmerAddress(product.getFarmer().getAddress());

        response.setProductName(product.getProductName());
        response.setCategory(product.getCategory());

        response.setPrice(product.getPrice());
        response.setUnit(product.getUnit());

        response.setDescription(product.getDescription());
        response.setProductPhoto(product.getProductPhoto());

        response.setCreatedBy(product.getCreatedBy());
        response.setCreatedOn(product.getCreatedOn());

        response.setUpdatedBy(product.getUpdatedBy());
        response.setUpdatedOn(product.getUpdatedOn());

        response.setIsActive(product.getIsActive());
        response.setIsDelete(product.getIsDelete());
        response.setStatus(product.getStatus());

        return response;
    }
    @Override
    public ProductResponse updateProduct(UUID id, ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        FarmerProfile farmer = farmerProfileRepository.findById(request.getFarmerId())
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        product.setFarmer(farmer);
        product.setProductName(request.getProductName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setUnit(request.getUnit());
        product.setDescription(request.getDescription());
        product.setProductPhoto(request.getProductPhoto());

        product.setUpdatedBy(farmer.getName());
        product.setUpdatedOn(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);

        ProductResponse response = new ProductResponse();

        response.setProductId(updatedProduct.getProductId());

        response.setFarmerId(updatedProduct.getFarmer().getId()); // Agar field farmerId hai to getFarmerId() use karo.
        response.setFarmerName(updatedProduct.getFarmer().getName());
        response.setFarmerAddress(updatedProduct.getFarmer().getAddress());

        response.setProductName(updatedProduct.getProductName());
        response.setCategory(updatedProduct.getCategory());
        response.setPrice(updatedProduct.getPrice());
        response.setUnit(updatedProduct.getUnit());

        response.setDescription(updatedProduct.getDescription());
        response.setProductPhoto(updatedProduct.getProductPhoto());

        response.setCreatedBy(updatedProduct.getCreatedBy());
        response.setCreatedOn(updatedProduct.getCreatedOn());

        response.setUpdatedBy(updatedProduct.getUpdatedBy());
        response.setUpdatedOn(updatedProduct.getUpdatedOn());

        response.setIsActive(updatedProduct.getIsActive());
        response.setIsDelete(updatedProduct.getIsDelete());
        response.setStatus(updatedProduct.getStatus());

        return response;
    }

    @Override
    public String deleteProduct(UUID id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setIsDelete(true);
        product.setIsActive(false);
        product.setStatus("DELETED");
        product.setUpdatedOn(LocalDateTime.now());

        productRepository.save(product);

        return "Product deleted successfully";
    }

    @Override
    public String uploadPhoto(MultipartFile photo) {

        if (photo.isEmpty()) {
            throw new RuntimeException("Please select a photo");
        }

        File folder = new File(uploadDir);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        String originalFileName = photo.getOriginalFilename();

        String extension = "";

        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        String fileName = UUID.randomUUID() + extension;

        File destination = new File(folder, fileName);

        System.out.println("Upload Dir = " + folder.getAbsolutePath());
        System.out.println("Saving File = " + destination.getAbsolutePath());

        try {

            Files.copy(
                    photo.getInputStream(),
                    destination.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );

            System.out.println("File Exists = " + destination.exists());

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException("Photo upload failed");

        }

        return fileName;
    }
    @Override
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByProductNameContainingIgnoreCase(keyword);
    }
};



