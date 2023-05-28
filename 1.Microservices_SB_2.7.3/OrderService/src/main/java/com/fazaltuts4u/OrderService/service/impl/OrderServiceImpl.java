package com.fazaltuts4u.OrderService.service.impl;

import com.fazaltuts4u.OrderService.entity.Order;
import com.fazaltuts4u.OrderService.model.OrderRequest;
import com.fazaltuts4u.OrderService.repository.OrderRepository;
import com.fazaltuts4u.OrderService.service.OrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public long placeOrder(OrderRequest orderRequest) {
    //Order Entity -> save the data with status Order Created
        //Prodcut Service - block products (reduce the quantity)
        //Payment Service - Payments -> Success -> Complete else Cancelled
        log.info("Placing Order Request: {}", orderRequest);
        Order order = Order.builder()
                .amount(orderRequest.getTotalAmount())
                .orderStatus("CREATED")
                .productId(orderRequest.getProductId())
                .orderDate(Instant.now())
                .quantity(orderRequest.getQuantity())
                .build();
        order = orderRepository.save(order);
        log.info("Order places successfully with Order Id: {}", order.getId());
        return order.getId();
    }
}
