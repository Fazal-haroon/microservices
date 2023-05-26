package com.fazaltuts4u.ProductService.service;

import com.fazaltuts4u.ProductService.model.ProductRequest;
import com.fazaltuts4u.ProductService.model.ProductResponse;

public interface ProductService {
    long addProduct(ProductRequest productRequest);

    ProductResponse getProductById(long productId);
}
