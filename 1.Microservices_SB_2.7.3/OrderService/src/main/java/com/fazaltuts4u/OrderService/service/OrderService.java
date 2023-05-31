package com.fazaltuts4u.OrderService.service;

import com.fazaltuts4u.OrderService.model.OrderRequest;
import com.fazaltuts4u.OrderService.model.OrderResponse;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetails(long orderId);
}
