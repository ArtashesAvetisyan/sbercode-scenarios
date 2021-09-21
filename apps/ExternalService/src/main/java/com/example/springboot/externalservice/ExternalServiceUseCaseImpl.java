package com.example.springboot.externalservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ExternalServiceUseCaseImpl implements ExternalServiceUseCase {

    @Value("${service.name}")
    private String serviceName;

    @Override
    public String getData() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("Hello from " + serviceName+ "! ")
                .append("\n");

        return stringBuilder.toString();
    }
}
