package com.fazaltuts4u.OrderService.service;

import com.fazaltuts4u.OrderService.model.OrderRequest;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);
}
