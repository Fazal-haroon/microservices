package com.fazaltuts4u.OrderService.service.impl;

import com.fazaltuts4u.OrderService.entity.Order;
import com.fazaltuts4u.OrderService.exception.CustomException;
import com.fazaltuts4u.OrderService.external.client.PaymentService;
import com.fazaltuts4u.OrderService.external.client.ProductService;
import com.fazaltuts4u.OrderService.external.request.PaymentRequest;
import com.fazaltuts4u.OrderService.external.response.PaymentResponse;
import com.fazaltuts4u.OrderService.external.response.ProductResponse;
import com.fazaltuts4u.OrderService.model.OrderRequest;
import com.fazaltuts4u.OrderService.model.OrderResponse;
import com.fazaltuts4u.OrderService.repository.OrderRepository;
import com.fazaltuts4u.OrderService.service.OrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public long placeOrder(OrderRequest orderRequest) {
    //Order Entity -> save the data with status Order Created
        //Prodcut Service - block products (reduce the quantity)
        //Payment Service - Payments -> Success -> Complete else Cancelled
        log.info("Placing Order Request: {}", orderRequest);

        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());

        log.info("Creating Order with Status CREATED");

        Order order = Order.builder()
                .amount(orderRequest.getTotalAmount())
                .orderStatus("CREATED")
                .productId(orderRequest.getProductId())
                .orderDate(Instant.now())
                .quantity(orderRequest.getQuantity())
                .build();
        order = orderRepository.save(order);
        log.info("Calling Payment Service to complete the payment");
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .orderId(order.getId())
                .paymentMode(orderRequest.getPaymentMode())
                .amount(orderRequest.getTotalAmount())
                .build();
        String orderStatus = null;
        try {
            paymentService.doPayment(paymentRequest);
            log.info("Payment done Successfully. Changing the order status to PLACED");
            orderStatus = "PLACED";
        } catch (Exception e) {
            log.info("Error occurred in payment. Changing order status to PAYMENT_FAILED");
            orderStatus = "PAYMENT_FAILED";
        }
        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
        log.info("Order places successfully with Order Id: {}", order.getId());
        return order.getId();
    }

    @Override
    public OrderResponse getOrderDetails(long orderId) {
        log.info("Get order details for Order Id: {}",orderId);
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new CustomException("Order not found for the Order Id: " + orderId, "NOT_FOUND", 404));

        log.info("Invoking Product Service to fetch the product for id: {}", order.getProductId());
        ProductResponse productResponse
                = restTemplate.getForObject(
                "http://PRODUCT-SERVICE/product/" + order.getProductId(),
                ProductResponse.class
        );

        log.info("Getting payment information form the payment Service");
        PaymentResponse paymentResponse
                = restTemplate.getForObject(
                "http://PAYMENT-SERVICE/payment/order/" + order.getId(),
                PaymentResponse.class
        );

        OrderResponse.ProductDetails productDetails = OrderResponse.ProductDetails.builder()
                .productId(productResponse.getProductId())
                .productName(productResponse.getProductName())
                .price(productResponse.getPrice())
                .quantity(productResponse.getQuantity())
                .build();

        OrderResponse.PaymentDetails paymentDetails = OrderResponse.PaymentDetails.builder()
                .paymentId(paymentResponse.getPaymentId())
                .paymentDate(paymentResponse.getPaymentDate())
                .paymentMode(paymentResponse.getPaymentMode())
                .status(paymentResponse.getStatus())
                .orderId(paymentResponse.getOrderId())
                .amount(paymentResponse.getAmount())
                .build();

        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(order.getId())
                .orderDate(order.getOrderDate())
                .amount(order.getAmount())
                .orderStatus(order.getOrderStatus())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();
        return orderResponse;
    }
}
