package com.wexa.cyphercart.service;

import com.wexa.cyphercart.dto.request.ProductCreateRequest;
import com.wexa.cyphercart.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    ProductResponse createProduct(ProductCreateRequest request);
    void deleteProduct(String id);
    List<ProductResponse> getRecommendations(String userId);
    List<ProductResponse> getProductRecommendations(String userId, String productId);
}