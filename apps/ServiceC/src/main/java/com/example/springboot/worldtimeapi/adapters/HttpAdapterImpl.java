package com.example.springboot.worldtimeapi.adapters;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpAdapterImpl implements Adapter {

    private final static String SERVICE_B_URL = "http://worldtimeapi.org/api/timezone/Europe";

    @Override
    public String get() {

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response
                    = restTemplate.getForEntity(SERVICE_B_URL, String.class);
            return "Received response from worldtimeapi.org: " + response.getBody().substring(0,100) + "... (printed only 100 symbols from response body beginning)";
        } catch (Exception exc){
            exc.printStackTrace();
            return exc.getMessage();
        }
    }
}
