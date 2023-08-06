package com.derster.cafesystem.service;

import com.derster.cafesystem.pojo.Product;
import com.derster.cafesystem.wrapper.ProductWrapper;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ProductService {
    ResponseEntity<String> addNewProduct(Map<String, String> requestMap);

    ResponseEntity<String> updateProduct(Map<String, String> requestMap);

    ResponseEntity<String> deleteProduct(Integer id);

    ResponseEntity<List<ProductWrapper>> getAllProduct();

    ResponseEntity<String> updateProductStatus(Map<String, String> requestMap);

    ResponseEntity<ProductWrapper> getProductById(Integer productId);

    ResponseEntity<List<ProductWrapper>> getProductByCategory(Integer categoryId);
}
