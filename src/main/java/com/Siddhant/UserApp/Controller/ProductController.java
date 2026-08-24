package com.Siddhant.UserApp.Controller;
import com.Siddhant.UserApp.Entity.Product;
import com.Siddhant.UserApp.Service.ProductService;
import com.Siddhant.UserApp.dto.ProductRequest;
import com.Siddhant.UserApp.dto.ProductResponse;
import com.Siddhant.UserApp.dto.ProductUpdateResponse;
import com.Siddhant.UserApp.mapper.MapperBuild;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
@Slf4j
@RestController
@RequestMapping("/product")
@CrossOrigin("*")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${file.upload-dir}")
    private String uploadDir;
    @PostMapping(value="/save", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductResponse saveProduct(@RequestPart("data") String data, @RequestPart(value="photo", required=false) MultipartFile photo, @RequestPart(value="video", required=false) MultipartFile video
    ) throws IOException {ProductRequest request = objectMapper.readValue(data, ProductRequest.class);return productService.saveProduct(request, photo, video);
    }
    @GetMapping("/getAll")
    public List<ProductResponse> getAllProducts() {return productService.getAllProducts();
    }
    @GetMapping("/getById/{id}")
    public ProductResponse getProductById(@PathVariable UUID id) {return productService.getProductById(id);
    }
    @PutMapping(value="/update/{id}", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductUpdateResponse updateProduct(@PathVariable UUID id, @RequestPart("data") String data, @RequestPart(value="photo", required=false) MultipartFile photo, @RequestPart(value="video", required=false) MultipartFile video
    ) throws IOException {ProductRequest request = objectMapper.readValue(data, ProductRequest.class);return productService.updateProduct(id, request, photo, video);
    }
    @DeleteMapping("/delete/{id}")
    public String deleteProduct(@PathVariable UUID id) {return productService.deleteProduct(id);
    }
    @PostMapping("/uploadPhoto/{id}")
    public String uploadPhoto(@PathVariable UUID id, @RequestParam("photo") MultipartFile photo) {return productService.uploadPhoto(id, photo);
    }
    @PostMapping("/uploadVideo/{id}")
    public String uploadVideo(@PathVariable UUID id, @RequestParam("video") MultipartFile video) {return productService.uploadVideo(id, video);
    }
    @GetMapping("/image/{fileName}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) throws IOException {Path path = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(fileName);Resource resource = new UrlResource(path.toUri());if (!resource.exists()) return ResponseEntity.notFound().build();String contentType = Files.probeContentType(path);if (contentType == null) contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"").body(resource);
    }

    @GetMapping("/video/{fileName}")
    public ResponseEntity<Resource> getVideo(@PathVariable String fileName) throws IOException {Path path = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(fileName);Resource resource = new UrlResource(path.toUri());
        if (!resource.exists()) return ResponseEntity.notFound().build();String contentType = Files.probeContentType(path);
        if (contentType == null) contentType = "video/mp4";return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"").body(resource);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String keyword
    ) {return ResponseEntity.ok(productService.searchProducts(keyword));
    }
    @GetMapping("/price")
    public ResponseEntity<List<ProductResponse>> filterByPrice(@RequestParam Double minPrice, @RequestParam Double maxPrice
    ) {return ResponseEntity.ok(productService.filterByPrice(minPrice, maxPrice));
    }
    @GetMapping("/filter")
    public ResponseEntity<List<ProductResponse>> filterByCategoryAndPrice(@RequestParam String category, @RequestParam Double minPrice, @RequestParam Double maxPrice
    ) {return ResponseEntity.ok(productService.filterByCategoryAndPrice(category, minPrice, maxPrice));
    }
    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<List<ProductResponse>> getProductsByFarmer(@PathVariable UUID farmerId) {return ResponseEntity.ok(
                productService.getProductsByFarmer(farmerId).stream().map(MapperBuild::buildProductResponse).toList());
    }
    @GetMapping("/active")
    public List<ProductResponse> getActiveProducts() {return productService.getActiveProducts();
    }
    @PatchMapping("/toggleStatus/{id}")
    public ResponseEntity<ProductResponse> toggleProductStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.toggleProductStatus(id));
    }
}

