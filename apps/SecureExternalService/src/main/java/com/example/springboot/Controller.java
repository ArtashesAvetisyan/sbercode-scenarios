package com.example.springboot;

import com.example.springboot.externalservice.ExternalServiceUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @Autowired
    private ExternalServiceUseCase externalServiceUseCase;

    @GetMapping("/greetings")
    public String getPayLoad() {
        return externalServiceUseCase.getData();
    }
}