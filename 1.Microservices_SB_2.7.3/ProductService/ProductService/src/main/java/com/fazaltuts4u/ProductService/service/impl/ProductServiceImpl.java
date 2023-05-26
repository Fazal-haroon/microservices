package com.fazaltuts4u.ProductService.service.impl;

import com.fazaltuts4u.ProductService.entity.Product;
import com.fazaltuts4u.ProductService.model.ProductRequest;
import com.fazaltuts4u.ProductService.repository.ProductRepository;
import com.fazaltuts4u.ProductService.service.ProductService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public long addProduct(ProductRequest productRequest) {
        log.info("Adding Product..");
        Product product = Product.builder()
                .productName(productRequest.getName())
                .quantity(productRequest.getQuantity())
                .price(productRequest.getPrice())
                .build();
        productRepository.save(product);
        log.info("Product Created..");
        return product.getProductId();
    }
}
