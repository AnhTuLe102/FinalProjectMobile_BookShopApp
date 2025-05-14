package com.bookstore.api.controller;

import com.bookstore.api.entity.product.BookType;
import com.bookstore.api.entity.product.Product;
import com.bookstore.api.response.ProductResponse;
import com.bookstore.api.service.ProductService;
import com.bookstore.api.util.S3Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public List<Product> getProducts() {
        return productService.findAll();
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable int productId) {

        ProductResponse response = productService.findById(productId);
        if (response.isError()) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/products")
    public ResponseEntity<ProductResponse> addProduct(@RequestBody Product theProduct) {

        ProductResponse response = productService.save(theProduct);
        if (response.isError()) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable int productId,
            @RequestBody Product theProduct) {

        ProductResponse response = productService.findById(productId);
        if (response.isError()) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        response = productService.update(productId, theProduct);
        if (response.isError()) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<ProductResponse> deleteProduct(@PathVariable int productId) {

        ProductResponse response = productService.findById(productId);
        if (response.isError()) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        response = productService.deleteById(productId);
        if (response.isError()) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/products/add-product")
    public ResponseEntity<ProductResponse> addProduct(@RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("book_type_id") int bookTypeId,
            @RequestParam("images") MultipartFile multipart) {

        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        // Gán book type
        BookType bookType = new BookType();
        bookType.setId(bookTypeId);
        product.setBookType(bookType);
        ProductResponse response = productService.save(product);
        if (response.isError()) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        Product theProduct = response.getProduct();

        try {
            String fileName = multipart.getOriginalFilename();
            assert fileName != null;
            String extension = fileName.substring(fileName.lastIndexOf("."));
            String newFileName = "product_" + "_" + System.currentTimeMillis() + extension;

            // Lưu vào thư mục static/uploads/
            Path uploadDir = Paths.get("uploads/");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Path filePath = uploadDir.resolve(newFileName);
            Files.copy(multipart.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Đường dẫn lưu vào DB (phía client dùng URL để hiển thị)
            String urlImage = "http://10.0.2.2:8080/uploads/" + newFileName;

            theProduct.setImages(urlImage);
            productService.save(theProduct); // <- Bổ sung dòng này

            response.setMessage("Update images successfully");
            response.setProduct(theProduct);

        } catch (IOException ex) {
            response.setError(true);
            response.setMessage("Error uploading file: " + ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/products/update-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> updateImages(@RequestParam("id") int productId,
            @RequestParam("images") MultipartFile multipart) {
        int productIdImg = productId;
        ProductResponse response = productService.findById(productId);
        if (response.isError()) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        try {
            String fileName = multipart.getOriginalFilename();
            assert fileName != null;
            String extension = fileName.substring(fileName.lastIndexOf("."));
            String newFileName = "product_" + productIdImg + "_" + System.currentTimeMillis() + extension;

            // Lưu vào thư mục static/uploads/
            Path uploadDir = Paths.get("uploads/");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Path filePath = uploadDir.resolve(newFileName);
            Files.copy(multipart.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Đường dẫn lưu vào DB (phía client dùng URL để hiển thị)
            String urlImage = "http://10.0.2.2:8080/uploads/" + newFileName;

            Product theProduct = response.getProduct();
            theProduct.setImages(urlImage);
            response = productService.update(productId, theProduct);
            response.setMessage("Update images successfully");
            response.setProduct(theProduct);
        } catch (IOException ex) {
            response.setError(true);
            response.setMessage("Error uploading file: " + ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/products/related/{bookTypeId}")
    public ResponseEntity<List<Product>> getRelatedProducts(@PathVariable int bookTypeId) {
        List<Product> products = productService.findByBookTypeId(bookTypeId);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
}
