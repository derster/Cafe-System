package com.derster.cafesystem.service.serviceImpl;

import com.derster.cafesystem.constants.CafeConstants;
import com.derster.cafesystem.dao.CategoryDao;
import com.derster.cafesystem.dao.ProductDao;
import com.derster.cafesystem.jwt.JwtFilter;
import com.derster.cafesystem.pojo.Category;
import com.derster.cafesystem.pojo.Product;
import com.derster.cafesystem.service.ProductService;
import com.derster.cafesystem.utils.CafeUtils;
import com.derster.cafesystem.wrapper.ProductWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductDao productDao;

    @Autowired
    CategoryDao categoryDao;
    @Autowired
    JwtFilter jwtFilter;
    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()){
                if (validateProductMap(requestMap, false)){
                    productDao.save(getProductFromMap(requestMap, false));

                    return  CafeUtils.getResponseEntity("Product Added Successfully", HttpStatus.OK);
                }
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }else{
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateProductMap(Map<String, String> requestMap, boolean validateId) {
        if(requestMap.containsKey("name")){
            if (requestMap.containsKey("id") && validateId){
                return true;
            }else if(!validateId) {
                return true;
            }
        }
        return false;
    }

    private Product getProductFromMap(Map<String, String> requestMap, Boolean isAdd){
        Product product = new Product();
        Category category = new Category();
        category.setId(Integer.parseInt(requestMap.get("category_id")));
        if (isAdd){
            product.setId(Integer.parseInt(requestMap.get("id")));
        }else {
            product.setStatus("true");
        }
        product.setCategory( category );
        product.setName(requestMap.get("name"));
        product.setDescription(requestMap.get("description"));
        product.setPrice( Integer.parseInt(requestMap.get("price")));
        product.setCreated_at( new Date());
        product.setUpdated_at( new Date());
        return product;
    }


    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()){
                if (validateProductMap(requestMap, true)){
                    Optional<Product> optional = productDao.findById(Integer.parseInt(requestMap.get("id")));

                    if (!optional.isEmpty()){
                        Product product = getProductFromMap(requestMap, true);
                        product.setStatus(optional.get().getStatus());
                        productDao.save(product);
                        return CafeUtils.getResponseEntity("product updated successfully", HttpStatus.OK);
                    }else{
                        return CafeUtils.getResponseEntity("product id does not exist", HttpStatus.OK);
                    }
                }
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }else{
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteProduct(Integer id) {

        if (jwtFilter.isAdmin()){
            Optional<Product> optional = productDao.findById(id);

            if (!optional.isEmpty()){
                productDao.deleteById(id);
                return CafeUtils.getResponseEntity("product deleted successfully", HttpStatus.OK);
            }else{
                return CafeUtils.getResponseEntity("product does not exist", HttpStatus.OK);
            }
            //return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
        }else{
            return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        }
    }


    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProduct() {
        return new ResponseEntity<>(productDao.getAllProduct(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> updateProductStatus(Map<String, String> requestMap) {
        if (jwtFilter.isAdmin()){


            Optional optional = productDao.findById(Integer.parseInt(requestMap.get("id")));

            if (!optional.isEmpty()){
                productDao.updateProductStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                return CafeUtils.getResponseEntity("Product Status updated successfully", HttpStatus.OK);
            }else{
                return CafeUtils.getResponseEntity("product does not exist", HttpStatus.OK);
            }
            //return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
        }else{
            return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public ResponseEntity<ProductWrapper> getProductById(Integer productId) {
        try {
            Optional optional = productDao.findById(productId);

            if (!optional.isEmpty()){
                return new ResponseEntity<>(productDao.getProductById(productId), HttpStatus.OK);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return new ResponseEntity<>(new ProductWrapper(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getProductByCategory(Integer categoryId) {
        try {
            return new ResponseEntity<>(productDao.getProductByCategory(categoryId), HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
 