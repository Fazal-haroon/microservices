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
import com.fazaltuts4u.OrderService.model.PaymentMode;
import com.fazaltuts4u.OrderService.repository.OrderRepository;
import com.fazaltuts4u.OrderService.service.OrderService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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


    @Value("${microservices.product}")
    private String productServiceUrl;

    @Value("${microservices.payment}")
    private String paymentServiceUrl;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils
                .setField(orderService, "productServiceUrl", productServiceUrl);
        ReflectionTestUtils
                .setField(orderService, "paymentServiceUrl", paymentServiceUrl);
    }

    @DisplayName("Get Order - Success Scenario")
    @Test
    void test_When_Order_Success() {
        //Mocking
        Order order = getMockOrder();
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.of(order));

        when(restTemplate.getForObject(
                productServiceUrl + order.getProductId(),
                ProductResponse.class)
        ).thenReturn(getMockProductResponse());

        when(restTemplate.getForObject(
                paymentServiceUrl + "order/" + order.getId(),
                PaymentResponse.class)
        ).thenReturn(getMockPaymentResponse());

        //Actual
        OrderResponse orderResponse = orderService.getOrderDetails(9);

        //Verification
        verify(orderRepository, times(1)).findById(anyLong());
        verify(restTemplate, times(1)).getForObject(
                productServiceUrl + order.getProductId(),
                ProductResponse.class
        );
        verify(restTemplate, times(1)).getForObject(
                paymentServiceUrl + "order/" + order.getId(),
                PaymentResponse.class
        );
        //Assert
        assertNotNull(orderResponse);
        assertEquals(order.getId(), orderResponse.getOrderId());
    }

    @DisplayName("Get Order - Failure Scenario")
    @Test
    void test_When_Get_Order_Not_Found_then_NOT_FOUND() {
        //Mocking
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(null));
        //Actual
        CustomException exception =
                assertThrows(CustomException.class,
                        () -> orderService.getOrderDetails(9));

        //Assert
        assertEquals("NOT_FOUND", exception.getErrorCode());
        assertEquals(404, exception.getStatus());

        //Verification
        verify(orderRepository, times(1)).findById(anyLong());
    }

    @DisplayName("Place Order - Success Scenario")
    @Test
    void test_When_Place_Order_Success() {
        Order order = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);
        when(productService.reduceQuantity(anyLong(),anyLong()))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
        when(paymentService.doPayment(any(PaymentRequest.class)))
                .thenReturn(new ResponseEntity<Long>(1L,HttpStatus.OK));

        long orderId = orderService.placeOrder(orderRequest);

        verify(orderRepository, times(2))
                .save(any());
        verify(productService, times(1))
                .reduceQuantity(anyLong(),anyLong());
        verify(paymentService, times(1))
                .doPayment(any(PaymentRequest.class));

        assertEquals(order.getId(), orderId);
    }

    @DisplayName("Place Order - Payment Failed Scenario")
    @Test
    void test_when_Place_Order_Payment_Fails_then_Order_Placed() {
        Order order = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);
        when(productService.reduceQuantity(anyLong(),anyLong()))
                .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
        when(paymentService.doPayment(any(PaymentRequest.class)))
                .thenThrow(new RuntimeException());

        long orderId = orderService.placeOrder(orderRequest);

        verify(orderRepository, times(2))
                .save(any());
        verify(productService, times(1))
                .reduceQuantity(anyLong(),anyLong());
        verify(paymentService, times(1))
                .doPayment(any(PaymentRequest.class));

        assertEquals(order.getId(), orderId);
    }

    private OrderRequest getMockOrderRequest() {
        return OrderRequest.builder()
                .productId(1)
                .quantity(10)
                .paymentMode(PaymentMode.CASH)
                .totalAmount(1000)
                .build();
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