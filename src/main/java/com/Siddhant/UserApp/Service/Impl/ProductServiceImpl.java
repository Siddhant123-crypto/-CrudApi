package com.Siddhant.UserApp.Service.Impl;
import com.Siddhant.UserApp.Entity.FarmerProfile;
import com.Siddhant.UserApp.Entity.Product;
import com.Siddhant.UserApp.Repository.FarmerProfileRepository;
import com.Siddhant.UserApp.Repository.ProductRepository;
import com.Siddhant.UserApp.Service.ProductService;
import com.Siddhant.UserApp.dto.ProductRequest;
import com.Siddhant.UserApp.dto.ProductResponse;
import com.Siddhant.UserApp.mapper.MapperBuild;
import com.Siddhant.UserApp.Service.NotificationService;
import com.Siddhant.UserApp.dto.NotificationSendRequest;
import com.Siddhant.UserApp.enums.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.Siddhant.UserApp.dto.ProductUpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);
    @Value("${file.upload-dir:uploads}")
    private String uploadDir;
    private final ProductRepository productRepository;
    private final FarmerProfileRepository farmerProfileRepository;
    private final NotificationService notificationService;
     @Autowired
    public ProductServiceImpl(ProductRepository productRepository, FarmerProfileRepository farmerProfileRepository, NotificationService notificationService) {
        this.productRepository = productRepository;
        this.farmerProfileRepository = farmerProfileRepository;
        this.notificationService = notificationService;
    }@Override
    public ProductResponse saveProduct(ProductRequest request, MultipartFile photo, MultipartFile video) {
        try {if (request == null) {throw new RuntimeException("Product data is required");
            }if (request.getFarmerId() == null) {throw new RuntimeException("Farmer ID is required");
            }if (request.getProductName() == null || request.getProductName().trim().isEmpty()) {throw new RuntimeException("Product name is required");
            }if (request.getPrice() == null || request.getPrice() < 0) {throw new RuntimeException("Valid product price is required");
            }if (request.getQuantity() == null || request.getQuantity() < 0) {throw new RuntimeException("Valid product quantity is required");
            }FarmerProfile farmer = farmerProfileRepository.findById(request.getFarmerId()).orElseThrow(() -> new RuntimeException("Farmer not found"));
            Product product = new Product();
            product.setFarmer(farmer);
            product.setProductName(request.getProductName().trim());
            product.setCategory(request.getCategory());
            product.setPrice(request.getPrice());
            product.setQuantity(request.getQuantity());
            product.setUnit(request.getUnit());
            product.setDescription(request.getDescription());
            product.setHarvestDate(request.getHarvestDate());
            if (photo != null && !photo.isEmpty()) {
                String photoFileName = saveFile(photo);
                product.setProductPhoto(photoFileName);
            }if (video != null && !video.isEmpty()) {String videoFileName = saveFile(video);
                product.setProductVideo(videoFileName);
            }String farmerName = farmer.getUser().getName();
            LocalDateTime now = LocalDateTime.now();
            product.setCreatedBy(farmerName);
            product.setCreatedOn(now);
            product.setAddedTime(now.toLocalTime());
            product.setUpdatedBy(farmerName);
            product.setUpdatedOn(now);
            product.setIsActive(true); // Active immediately
            product.setIsDelete(false);
            product.setStatus("ACTIVE");
            Product savedProduct = productRepository.save(product);
            log.info("Product saved successfully: {}", savedProduct.getProductId());
            return MapperBuild.buildProductResponse(savedProduct);
        } catch (Exception e) {log.error("Error saving product", e);
            throw new RuntimeException("Error saving product: " + e.getMessage());
        }
    }@Override
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findByIsActiveTrueAndIsDeleteFalse();
        log.debug("Total Products retrieved: {}", products.size());
        List<ProductResponse> responseList = new ArrayList<>();
        for (Product product : products) {
            if (product.getFarmer() == null || product.getFarmer().getUser() == null) {continue;}
            responseList.add(MapperBuild.buildProductResponse(product));
        }return responseList;
    }@Override
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        return MapperBuild.buildProductResponse(product);
    }@Override
    public ProductUpdateResponse updateProduct(UUID id, ProductRequest request, MultipartFile photo, MultipartFile video) {
        try {Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
            FarmerProfile farmer = farmerProfileRepository.findById(request.getFarmerId()).orElseThrow(() -> new RuntimeException("Farmer not found"));
            product.setFarmer(farmer);
            product.setProductName(request.getProductName().trim());
            product.setCategory(request.getCategory());
            product.setPrice(request.getPrice());
            product.setQuantity(request.getQuantity());
            product.setUnit(request.getUnit());
            product.setDescription(request.getDescription());
            product.setHarvestDate(request.getHarvestDate());
            if (photo != null && !photo.isEmpty()) {String photoFileName = saveFile(photo);
                product.setProductPhoto(photoFileName);
            }if (video != null && !video.isEmpty()) {
                String videoFileName = saveFile(video);
                product.setProductVideo(videoFileName);
            } else if (Boolean.TRUE.equals(request.getRemoveVideo())) {
                product.setProductVideo(null);
            }product.setUpdatedBy(farmer.getUser().getName());
            product.setUpdatedOn(LocalDateTime.now());
            Product updatedProduct = productRepository.save(product);
            log.info("Product updated successfully: {}", id);
            ProductResponse response = MapperBuild.buildProductResponse(updatedProduct);
            return new ProductUpdateResponse("Product updated successfully", response);
        } catch (Exception e) {log.error("Error updating product {}", id, e);
            throw new RuntimeException("Error updating product: " + e.getMessage());
        }
    }@Override
    public String deleteProduct(UUID id) {
        try {Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
            product.setIsDelete(true);
            product.setIsActive(false);
            product.setStatus("DELETED");
            product.setUpdatedOn(LocalDateTime.now());
            productRepository.save(product);
            log.info("Product deleted successfully: {}", id);
            return "Product deleted successfully";
        } catch (Exception e) {
            log.error("Error deleting product {}", id, e);
            return "Error deleting product: " + e.getMessage();
        }
    }private String saveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }try {
            File folder = new File(uploadDir);
            if (!folder.exists()) {
                boolean created = folder.mkdirs();
                if (!created && !folder.exists()) {
                    throw new RuntimeException("Unable to create upload directory"
                    );
                }log.info("Upload directory created: {}", uploadDir
                );
            }String originalFileName = file.getOriginalFilename();
            String extension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }String fileName = UUID.randomUUID() + extension;
            File destination = new File(folder, fileName);
            log.debug("Saving file to: {}", destination.getAbsolutePath());
            Files.copy(file.getInputStream(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            log.info("File uploaded successfully: {}", fileName);
            return fileName;
        } catch (Exception e) {
            log.error("File upload failed", e);
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }@Override
    public List<ProductResponse> searchProducts(String keyword) {
        List<Product> products = productRepository.findByProductNameContainingIgnoreCaseAndIsActiveTrueAndIsDeleteFalse(keyword);
        return products.stream()
            .filter(product -> product.getFarmer() != null && product.getFarmer().getUser() != null)
            .map(MapperBuild::buildProductResponse).toList();
    }@Override
    public List<ProductResponse> findByPriceBetween(Double minPrice, Double maxPrice) {
        return productRepository.findByPriceBetweenAndIsActiveTrueAndIsDeleteFalse(minPrice, maxPrice).stream()
            .filter(product -> product.getFarmer() != null && product.getFarmer().getUser() != null)
            .map(MapperBuild::buildProductResponse).toList();
    }@Override
    public List<ProductResponse> filterByPrice(Double minPrice, Double maxPrice) {
        return productRepository.findByPriceBetweenAndIsActiveTrueAndIsDeleteFalse(minPrice, maxPrice).stream()
            .filter(product -> product.getFarmer() != null && product.getFarmer().getUser() != null)
            .map(MapperBuild::buildProductResponse).toList();
    }@Override
    public List<ProductResponse> filterByCategoryAndPrice(String category, Double minPrice, Double maxPrice) {
        return productRepository.findByCategoryIgnoreCaseAndPriceBetweenAndIsActiveTrueAndIsDeleteFalse(category, minPrice, maxPrice).stream()
            .filter(product -> product.getFarmer() != null && product.getFarmer().getUser() != null)
            .map(MapperBuild::buildProductResponse).toList();
    }@Override
    public List<Product> getProductsByFarmer(UUID farmerId) {FarmerProfile farmer = farmerProfileRepository.findById(farmerId).orElseThrow(() -> new RuntimeException("Farmer not found"));return productRepository.findByFarmer(farmer);}
    @Override
    public List<ProductResponse> getActiveProducts() {
        return productRepository.findByIsActiveTrueAndIsDeleteFalse().stream()
            .filter(product -> product.getFarmer() != null && product.getFarmer().getUser() != null)
            .map(MapperBuild::buildProductResponse).toList();
    }@Override
    public ProductResponse toggleProductStatus(UUID id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        if ("ACTIVE".equalsIgnoreCase(product.getStatus()) || Boolean.TRUE.equals(product.getIsActive())) {
            product.setIsActive(false);
            product.setStatus("INACTIVE");
        } else {
            product.setIsActive(true);
            product.setStatus("ACTIVE");
        }product.setUpdatedOn(LocalDateTime.now());
        Product savedProduct = productRepository.save(product);
        log.info("Product status toggled to {}: {}", savedProduct.getStatus(), id);
        return MapperBuild.buildProductResponse(savedProduct);
    }@Override
    public String uploadPhoto(UUID productId, MultipartFile photo) {Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));String fileName = saveFile(photo);product.setProductPhoto(fileName);product.setUpdatedOn(LocalDateTime.now());productRepository.save(product);return "Product photo uploaded successfully";}
    @Override
    public String uploadVideo(UUID productId, MultipartFile video) {Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));String fileName = saveFile(video);product.setProductVideo(fileName);product.setUpdatedOn(LocalDateTime.now());productRepository.save(product);return "Product video uploaded successfully";
    }

}