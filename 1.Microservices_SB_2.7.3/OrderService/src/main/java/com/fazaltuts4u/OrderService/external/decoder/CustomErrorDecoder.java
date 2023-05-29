package com.fazaltuts4u.OrderService.external.decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fazaltuts4u.OrderService.external.response.ErrorResponse;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

@Log4j2
public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String s, Response response) {
        ObjectMapper objectMapper = new ObjectMapper();

        log.info("::{}", response.request().url());
        log.info("::{}", response.request().headers());

        try {
            ErrorResponse errorResponse = objectMapper.readValue(response.body().asInputStream(), ErrorResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
