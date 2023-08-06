package com.derster.cafesystem.dao;

import com.derster.cafesystem.pojo.Product;
import com.derster.cafesystem.wrapper.ProductWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Repository
public interface ProductDao extends JpaRepository<Product, Integer> {
    List<ProductWrapper> getAllProduct();
    @Modifying
    @Transactional
    Integer updateProductStatus(@Param("status") String status, @Param("id") Integer id);

    ProductWrapper getProductById(Integer productId);

    List<ProductWrapper> getProductByCategory(Integer categoryId);
}
