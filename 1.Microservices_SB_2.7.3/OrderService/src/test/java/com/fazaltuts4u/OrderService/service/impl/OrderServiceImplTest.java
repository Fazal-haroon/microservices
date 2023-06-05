package com.fazaltuts4u.OrderService.service.impl;

import com.fazaltuts4u.OrderService.entity.Order;
import com.fazaltuts4u.OrderService.external.client.PaymentService;
import com.fazaltuts4u.OrderService.external.client.ProductService;
import com.fazaltuts4u.OrderService.external.response.PaymentResponse;
import com.fazaltuts4u.OrderService.external.response.ProductResponse;
import com.fazaltuts4u.OrderService.model.OrderResponse;
import com.fazaltuts4u.OrderService.model.PaymentMode;
import com.fazaltuts4u.OrderService.repository.OrderRepository;
import com.fazaltuts4u.OrderService.service.OrderService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    OrderService orderService = new OrderServiceImpl();

    @DisplayName("Get Order - Success Scenario")
    @Test
    void test_When_Order_Success(){
        //Mocking
        Order order = getMockOrder();
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(order));

        when(restTemplate.getForObject(
                "http://PRODUCT-SERVICE/product/" + order.getProductId(),
                ProductResponse.class)
        ).thenReturn(getMockProductResponse());

        when(restTemplate.getForObject(
                "http://PAYMENT-SERVICE/payment/order/" + order.getId(),
                PaymentResponse.class)
        ).thenReturn(getMockPaymentResponse());
        //Actual
        OrderResponse orderResponse = orderService.getOrderDetails(9);
        //Verification
        verify(orderRepository, times(1)).findById(anyLong());
        verify(restTemplate, times(1)).getForObject(
                "http://PRODUCT-SERVICE/product/" + order.getProductId(),
                ProductResponse.class
        );
        verify(restTemplate, times(1)).getForObject(
                "http://PAYMENT-SERVICE/payment/order/" + order.getId(),
                PaymentResponse.class
        );
        //Assert
        assertNotNull(orderResponse);
        assertEquals(order.getId(), orderResponse.getOrderId());
    }

    private PaymentResponse getMockPaymentResponse() {
        return PaymentResponse.builder()
                .orderId(9)
                .paymentDate(Instant.now())
                .amount(100)
                .paymentMode(PaymentMode.CASH)
                .status("ACCEPTED")
                .paymentId(1)
                .build();
    }

    private ProductResponse getMockProductResponse() {
        return ProductResponse.builder()
                .productId(2)
                .productName("iPhone")
                .price(100)
                .quantity(200)
                .build();
    }

    private Order getMockOrder() {
        return Order.builder()
                .orderStatus("PLACED")
                .orderDate(Instant.now())
                .id(9)
                .amount(100)
                .quantity(200)
                .productId(2)
                .build();
    }

}