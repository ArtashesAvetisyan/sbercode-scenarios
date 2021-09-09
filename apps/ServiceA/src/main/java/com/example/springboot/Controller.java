package com.example.springboot;

import com.example.springboot.serviceb.ServiceBUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @Autowired
    private ServiceBUseCase serviceBUseCase;

    @GetMapping("/")
    public String index() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("Hello from ServiceA! ")
                .append("Calling Producer Service... ")
                .append(serviceBUseCase.getDataFromServiceB())
                .append("\n");

        return stringBuilder.toString();

    }
}