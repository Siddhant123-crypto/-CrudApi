package com.Siddhant.UserApp.Service.Impl;
import com.Siddhant.UserApp.Entity.FarmerProfile;
import com.Siddhant.UserApp.Entity.Product;
import com.Siddhant.UserApp.Repository.FarmerProfileRepository;
import com.Siddhant.UserApp.Repository.ProductRepository;
import com.Siddhant.UserApp.Service.ProductService;
import com.Siddhant.UserApp.dto.ProductRequest;
import com.Siddhant.UserApp.dto.ProductResponse;
import com.Siddhant.UserApp.mapper.MapperBuild;
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
    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, FarmerProfileRepository farmerProfileRepository) {
        this.productRepository = productRepository;
        this.farmerProfileRepository = farmerProfileRepository;
    }
    // =========================================================
    // SAVE PRODUCT
    // =========================================================
    @Override
    public ProductResponse saveProduct(ProductRequest request, MultipartFile photo, MultipartFile video) {
        try {
            // -------------------------------------------------
            // VALIDATION
            // -------------------------------------------------
            if (request == null) {throw new RuntimeException("Product data is required");
            }
            if (request.getFarmerId() == null) {throw new RuntimeException("Farmer ID is required");
            }
            if (request.getProductName() == null || request.getProductName().trim().isEmpty()) {throw new RuntimeException("Product name is required");
            }
            if (request.getPrice() == null || request.getPrice() < 0) {throw new RuntimeException("Valid product price is required");
            }
            if (request.getQuantity() == null || request.getQuantity() < 0) {throw new RuntimeException("Valid product quantity is required");
            }
            // -------------------------------------------------
            // FIND FARMER
            // -------------------------------------------------
            FarmerProfile farmer = farmerProfileRepository.findById(request.getFarmerId()).orElseThrow(() -> new RuntimeException("Farmer not found"));
            // -------------------------------------------------
            // CREATE PRODUCT
            // -------------------------------------------------
            Product product = new Product();
            product.setFarmer(farmer);
            product.setProductName(request.getProductName().trim());
            product.setCategory(request.getCategory());
            product.setPrice(request.getPrice());
            product.setQuantity(request.getQuantity());
            product.setUnit(request.getUnit());
            product.setDescription(request.getDescription());
            product.setHarvestDate(request.getHarvestDate());
            // -------------------------------------------------
            // PHOTO
            // -------------------------------------------------
            if (photo != null && !photo.isEmpty()) {
                String photoFileName = saveFile(photo);
                product.setProductPhoto(photoFileName);
            }
            // -------------------------------------------------
            // OPTIONAL VIDEO
            // -------------------------------------------------
            if (video != null && !video.isEmpty()) {String videoFileName = saveFile(video);
                product.setProductVideo(videoFileName);
            }
            // -------------------------------------------------
            // AUDIT FIELDS
            // -------------------------------------------------
            String farmerName = farmer.getUser().getName();
            LocalDateTime now = LocalDateTime.now();
            product.setCreatedBy(farmerName);
            product.setCreatedOn(now);
            product.setUpdatedBy(farmerName);
            product.setUpdatedOn(now);
            product.setIsActive(true);
            product.setIsDelete(false);
            product.setStatus("ACTIVE");
            // -------------------------------------------------
            // SAVE
            // -------------------------------------------------
            Product savedProduct = productRepository.save(product);
            log.info("Product saved successfully: {}", savedProduct.getProductId());
            return MapperBuild.buildProductResponse(savedProduct);
        } catch (Exception e) {
            log.error("Error saving product", e);
            throw new RuntimeException("Error saving product: " + e.getMessage());
        }
    }
    // =========================================================
    // GET ALL PRODUCTS
    // =========================================================
    @Override
    public List<ProductResponse> getAllProducts() {List<Product> products = productRepository.findAll();
        log.debug("Total Products retrieved: {}", products.size());
        List<ProductResponse> responseList = new ArrayList<>();
        for (Product product : products) {if (Boolean.TRUE.equals(product.getIsDelete())) {continue;}
            responseList.add(MapperBuild.buildProductResponse(product));
        }
        return responseList;
    }
    // =========================================================
    // GET PRODUCT BY ID
    // =========================================================
    @Override
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        return MapperBuild.buildProductResponse(product);
    }
    // =========================================================
    // UPDATE PRODUCT
    // =========================================================
    @Override
    public ProductUpdateResponse updateProduct(UUID id, ProductRequest request, MultipartFile photo, MultipartFile video) {
        try {
            // -------------------------------------------------
            // FIND PRODUCT
            // -------------------------------------------------
            Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
            // -------------------------------------------------
            // FIND FARMER
            // -------------------------------------------------
            FarmerProfile farmer = farmerProfileRepository.findById(request.getFarmerId()).orElseThrow(() -> new RuntimeException("Farmer not found"));
            // -------------------------------------------------
            // UPDATE PRODUCT DETAILS
            // -------------------------------------------------
            product.setFarmer(farmer);
            product.setProductName(request.getProductName().trim());
            product.setCategory(request.getCategory());
            product.setPrice(request.getPrice());
            product.setQuantity(request.getQuantity());
            product.setUnit(request.getUnit());
            product.setDescription(request.getDescription());
            product.setHarvestDate(request.getHarvestDate());
            // -------------------------------------------------
            // UPDATE PHOTO ONLY IF NEW PHOTO PROVIDED
            // -------------------------------------------------
            if (photo != null && !photo.isEmpty()) {String photoFileName = saveFile(photo);
                product.setProductPhoto(photoFileName);
            }
            // -------------------------------------------------
            // UPDATE VIDEO ONLY IF NEW VIDEO PROVIDED
            // -------------------------------------------------
            if (video != null && !video.isEmpty()) {
                String videoFileName = saveFile(video);
                product.setProductVideo(videoFileName);
            } else if (Boolean.TRUE.equals(request.getRemoveVideo())) {
                product.setProductVideo(null);
            }
            // -------------------------------------------------
            // UPDATE AUDIT DATA
            // -------------------------------------------------
            product.setUpdatedBy(farmer.getUser().getName());
            product.setUpdatedOn(LocalDateTime.now());
            // -------------------------------------------------
            // SAVE UPDATED PRODUCT
            // -------------------------------------------------
            Product updatedProduct = productRepository.save(product);
            log.info("Product updated successfully: {}", id);
            // -------------------------------------------------
            // BUILD RESPONSE
            // -------------------------------------------------
            ProductResponse response = MapperBuild.buildProductResponse(updatedProduct);
            // -------------------------------------------------
            // RETURN MESSAGE + RESPONSE
            // -------------------------------------------------
            return new ProductUpdateResponse("Product updated successfully", response);
        } catch (Exception e) {log.error("Error updating product {}", id, e);
            throw new RuntimeException("Error updating product: " + e.getMessage());
        }
    }
    // =========================================================
    // DELETE PRODUCT
    // =========================================================
    @Override
    public String deleteProduct(UUID id) {
        try {
            Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
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
    }
    // =========================================================
    // SAVE FILE
    // =========================================================
    private String saveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        try {
            File folder = new File(uploadDir);
            if (!folder.exists()) {
                boolean created = folder.mkdirs();
                if (!created && !folder.exists()) {
                    throw new RuntimeException("Unable to create upload directory"
                    );
                }
                log.info("Upload directory created: {}", uploadDir
                );
            }
            String originalFileName = file.getOriginalFilename();
            String extension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID() + extension;
            File destination = new File(folder, fileName);
            log.debug("Saving file to: {}", destination.getAbsolutePath());
            Files.copy(file.getInputStream(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            log.info("File uploaded successfully: {}", fileName);
            return fileName;
        } catch (Exception e) {
            log.error("File upload failed", e);
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }
    // =========================================================
    // SEARCH PRODUCTS
    // =========================================================
    @Override
    public List<ProductResponse> searchProducts(String keyword) {List<Product> products = productRepository.findByProductNameContainingIgnoreCase(keyword);return products.stream().filter(product -> !Boolean.TRUE.equals(product.getIsDelete())).map(MapperBuild::buildProductResponse).toList();}
    // =========================================================
    // PRICE RANGE
    // =========================================================
    @Override
    public List<ProductResponse> findByPriceBetween(Double minPrice, Double maxPrice
    ) {
        return productRepository.findByPriceBetween(minPrice, maxPrice).stream().filter(product -> !Boolean.TRUE.equals(product.getIsDelete())).map(MapperBuild::buildProductResponse).toList();
    }
    @Override
    public List<ProductResponse> filterByPrice(Double minPrice, Double maxPrice
    ) {
        return productRepository.findByPriceBetween(minPrice, maxPrice).stream().filter(product -> !Boolean.TRUE.equals(product.getIsDelete())).map(MapperBuild::buildProductResponse).toList();
    }
    @Override
    public List<ProductResponse> filterByCategoryAndPrice(String category, Double minPrice, Double maxPrice
    ) {
        return productRepository.findByCategoryIgnoreCaseAndPriceBetween(category, minPrice, maxPrice).stream().filter(product -> !Boolean.TRUE.equals(product.getIsDelete())).map(MapperBuild::buildProductResponse).toList();
    }    // =========================================================
    // PRODUCTS BY FARMER
    // =========================================================
    @Override
    public List<Product> getProductsByFarmer(UUID farmerId) {FarmerProfile farmer = farmerProfileRepository.findById(farmerId).orElseThrow(() -> new RuntimeException("Farmer not found"));return productRepository.findByFarmer(farmer);}
    // =========================================================
    // ACTIVE PRODUCTS
    // =========================================================
    @Override
    public List<ProductResponse> getActiveProducts() {return productRepository.findByIsActiveTrueAndIsDeleteFalse().stream().map(MapperBuild::buildProductResponse).toList();}
    // =========================================================
    // TOGGLE STATUS
    // =========================================================
    @Override
    public ProductResponse toggleProductStatus(UUID id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if ("ACTIVE".equalsIgnoreCase(product.getStatus()) || Boolean.TRUE.equals(product.getIsActive())) {
            product.setIsActive(false);
            product.setStatus("INACTIVE");
        } else {
            product.setIsActive(true);
            product.setStatus("ACTIVE");
        }
        product.setUpdatedOn(LocalDateTime.now());
        Product savedProduct = productRepository.save(product);
        log.info("Product status toggled to {}: {}", savedProduct.getStatus(), id);
        return MapperBuild.buildProductResponse(savedProduct);
    }
    // =========================================================
    // UPLOAD PHOTO SEPARATELY
    // =========================================================
    @Override
    public String uploadPhoto(UUID productId, MultipartFile photo) {Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));String fileName = saveFile(photo);product.setProductPhoto(fileName);product.setUpdatedOn(LocalDateTime.now());productRepository.save(product);return "Product photo uploaded successfully";}
    // =========================================================
    // UPLOAD VIDEO SEPARATELY
    // =========================================================
    @Override
    public String uploadVideo(UUID productId, MultipartFile video) {Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));String fileName = saveFile(video);product.setProductVideo(fileName);product.setUpdatedOn(LocalDateTime.now());productRepository.save(product);return "Product video uploaded successfully";
    }
}