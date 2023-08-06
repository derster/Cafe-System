package com.derster.cafesystem.controller;

import com.derster.cafesystem.wrapper.ProductWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping(path = "/product")
public interface ProductController {
    @PostMapping(path = "/add")
    ResponseEntity<String> addNewProduct(@RequestBody  Map<String, String> requestMap);

    @GetMapping(path = "/get")
    ResponseEntity<List<ProductWrapper>> getAllProduct();

    @PostMapping(path = "/update")
    ResponseEntity<String> updateProduct(@RequestBody Map<String, String> requestMap);

    @DeleteMapping(path = "/delete/{id}")
    ResponseEntity<String> deleteProduct(@PathVariable Integer id);

    @PostMapping(path = "/updateProductStatus")
    ResponseEntity<String> updateProductStatus(@RequestBody Map<String, String> requestMap);

    @GetMapping(path = "/getProductByCategory/{categoryId}")
    ResponseEntity<List<ProductWrapper>> getProductByCategory(@PathVariable Integer categoryId);

    @GetMapping(path = "/getProductById/{productId}")
    ResponseEntity<ProductWrapper> getProductById(@PathVariable Integer productId);

}
