package com.wexa.cyphercart.service.impl;

import com.wexa.cyphercart.dto.request.ProductCreateRequest;
import com.wexa.cyphercart.dto.response.ProductResponse;
import com.wexa.cyphercart.entity.Product;
import com.wexa.cyphercart.repository.ProductRepository;
import com.wexa.cyphercart.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public ProductResponse createProduct(ProductCreateRequest request) {
        long totalProducts = productRepository.count();
        String newId = "prod-" + (totalProducts + 1);
        
        Product savedProduct = productRepository.createProductCypher(
            newId,
            request.getName(),
            request.getPrice(),
            request.getCategory(),
            request.getBrand(),
            request.getStock(),
            request.getStatus()
        );

        return mapToResponse(savedProduct);
    }

    @Override
    public void deleteProduct(String id) {
        productRepository.deleteProductSafely(id);
    }

    @Override
    public List<ProductResponse> getRecommendations(String userId) {
        List<Product> recommendations = productRepository.getGadgetRecommendations(userId);
        return recommendations.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getProductRecommendations(String userId, String productId) {
        List<Product> recommendations = productRepository.getProductRecommendations(userId, productId);
        return recommendations.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private ProductResponse mapToResponse(Product product) {
        if (product == null) return null;
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setCategory(product.getCategory());
        response.setBrand(product.getBrand());
        response.setStock(product.getStock());
        response.setStatus(product.getStatus());
        return response;
    }
}