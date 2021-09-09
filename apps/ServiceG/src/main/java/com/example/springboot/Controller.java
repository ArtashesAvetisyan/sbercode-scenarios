package com.example.springboot;

import com.example.springboot.mastersystem.MasterSystemUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @Value("${service.name}")
    private String serviceName;

    @Autowired
    private MasterSystemUseCase masterSystemUseCase;

    @GetMapping("/")
    public String index() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("Hello from " + serviceName+ "! ")
                .append("Calling master system API... ")
                .append(masterSystemUseCase.getData())
                .append("\n");

        return stringBuilder.toString();

    }
}