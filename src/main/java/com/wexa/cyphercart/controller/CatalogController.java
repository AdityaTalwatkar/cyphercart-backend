package com.wexa.cyphercart.controller;

import com.wexa.cyphercart.dto.response.ProductResponse;
import com.wexa.cyphercart.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final ProductService productService;

    public CatalogController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getCatalog() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/recommendations/{userId}")
    public ResponseEntity<List<ProductResponse>> getRecommendationsForUser(@PathVariable String userId) {
        return ResponseEntity.ok(productService.getRecommendations(userId));
    }

    
    @GetMapping("/recommendations/{userId}/{productId}")
    public ResponseEntity<List<ProductResponse>> getProductRecommendations(
            @PathVariable String userId, 
            @PathVariable String productId) {
        return ResponseEntity.ok(productService.getProductRecommendations(userId, productId));
    }
}