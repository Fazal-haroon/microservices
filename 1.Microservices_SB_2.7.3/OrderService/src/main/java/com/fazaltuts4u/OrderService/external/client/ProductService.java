package com.fazaltuts4u.OrderService.external.client;

import com.fazaltuts4u.OrderService.exception.CustomException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@CircuitBreaker(name = "external", fallbackMethod = "fallback")
//@FeignClient(name = "PRODUCT-SERVICE/product")
@FeignClient(name = "product", url = "${microservices.product}")
public interface ProductService {
    @PutMapping("/reduceQuantity/{id}")
    ResponseEntity<Void> reduceQuantity(@PathVariable("id") long productId, @RequestParam long quantity);

    default ResponseEntity<Void> fallback(Exception e){
        throw new CustomException("Product Service is not Available", "UNAVAILABLE", 500);
    }
}
