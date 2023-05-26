package com.fazaltuts4u.ProductService.repository;

import com.fazaltuts4u.ProductService.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
