package com.fazaltuts4u.OrderService.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fazaltuts4u.OrderService.OrderServiceConfig;
import com.fazaltuts4u.OrderService.model.OrderRequest;
import com.fazaltuts4u.OrderService.model.PaymentMode;
import com.fazaltuts4u.OrderService.repository.OrderRepository;
import com.fazaltuts4u.OrderService.service.OrderService;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.nio.charset.Charset.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.util.StreamUtils.*;

@SpringBootTest({"server.port=0"})
@EnableConfigurationProperties
@AutoConfigureMockMvc
@ContextConfiguration(classes = {OrderServiceConfig.class})
public class OrderControllerTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MockMvc mockMvc;

    @RegisterExtension
    static WireMockExtension wireMockExtension
            = WireMockExtension.newInstance()
            .options(WireMockConfiguration
                    .wireMockConfig()
                    .port(8080))
            .build();

    private ObjectMapper objectMapper
            = new ObjectMapper()
            .findAndRegisterModules()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @BeforeEach
    void setup() throws IOException {
        getProductDetailsResponse();
        doPayment();
        getPaymentDetails();
        reduceQuantity();
    }

    private void doPayment() {
        wireMockExtension.stubFor(post(urlEqualTo("/payment"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));
    }

    private void getPaymentDetails() throws IOException {
        wireMockExtension.stubFor(get(urlMatching("/payment/.*"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                copyToString(
                                        OrderControllerTest.class
                                                .getClassLoader()
                                                .getResourceAsStream("mock/GetPayment.json"),
                                        defaultCharset()
                                )
                        )));
    }

    private void reduceQuantity() {
        wireMockExtension.stubFor(put(urlMatching("/product/reduceQuantity/.*"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));
    }

    private void getProductDetailsResponse() throws IOException {
        // GET /product/1
        wireMockExtension.stubFor(get("/product/1")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(copyToString(
                                OrderControllerTest.class
                                        .getClassLoader()
                                        .getResourceAsStream("mock/GetProduct.json"),
                                defaultCharset()
                        )))
                );
    }

    private OrderRequest getMockOrderRequest() {
        return OrderRequest.builder()
                .productId(1)
                .paymentMode(PaymentMode.CASH)
                .quantity(10)
                .totalAmount(200)
                .build();
    }

    @Test
    public void test_WhenPlaceOrder_DoPayment_Success() throws JsonProcessingException {
        //First Place Order
        //Get Order by OrderId from DB and check
        //Check Output
        OrderRequest orderRequest = getMockOrderRequest();
        MvcResult mvcResult
                = mockMvc.perform(MockMvcRequestBuilders.post("/order/placeOrder")
                        .with(jwt().authorities(new SimpleGrantedAuthority("Customer")))
                        .content(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(orderRequest))
                ).andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }
}