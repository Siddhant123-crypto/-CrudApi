package com.Siddhant.UserApp.Controller;

import com.Siddhant.UserApp.Entity.Product;
import com.Siddhant.UserApp.Service.ProductService;
import com.Siddhant.UserApp.dto.ProductRequest;
import com.Siddhant.UserApp.dto.ProductResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/product")
@CrossOrigin("*")
public class ProductController {

    @Autowired
    private ProductService productService;


    @Value("${file.upload-dir}")
    private String uploadDir;


    @PostMapping("/save")
    public ProductResponse saveProduct(@RequestBody ProductRequest request) {

        return productService.saveProduct(request);
    }


    @GetMapping("/getAll")
    public List<ProductResponse> getAllProducts() {

        return productService.getAllProducts();
    }

    @GetMapping("/getById/{id}")
    public ProductResponse getProductById(@PathVariable UUID id) {

        return productService.getProductById(id);
    }


    @PutMapping("/update/{id}")
    public ProductResponse updateProduct(
            @PathVariable UUID id,
            @RequestBody ProductRequest request) {

        return productService.updateProduct(id, request);
    }


    @DeleteMapping("/delete/{id}")
    public String deleteProduct(@PathVariable UUID id) {

        return productService.deleteProduct(id);
    }


    // Upload Product Photo
    @PostMapping("/upload")
    public String uploadPhoto(@RequestParam("photo") MultipartFile photo) {

        return productService.uploadPhoto(photo);
    }


    @GetMapping("/image/{fileName}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) throws IOException {

        System.out.println("Requested Image = " + fileName);

        Path path = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize()
                .resolve(fileName);

        System.out.println("Upload Dir = " + uploadDir);
        System.out.println("Full Path = " + path);
        System.out.println("Exists = " + path.toFile().exists());

        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            throw new RuntimeException("Image not found");
        }

        return ResponseEntity.ok().body(resource);
    }
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(
            @RequestParam String keyword) {

        List<Product> products = productService.searchProducts(keyword);

        return ResponseEntity.ok(products);
    }
}