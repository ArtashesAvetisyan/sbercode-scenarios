package com.example.springboot;

import com.example.springboot.worldtimeapi.WorldTimeUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @Autowired
    private WorldTimeUseCase worldTimeUseCase;

    @GetMapping("/")
    public String index() {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("Hello from ServiceC! ")
                .append("Calling worldtimeapi.org API... ")
                .append(worldTimeUseCase.getDataFromWorldTime())
                .append("\n");

        return stringBuilder.toString();

    }
}