package com.example.springboot.serviceb.adapters;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpAdapterImpl implements Adapter {

    private final static String SERVICE_B_URL = "http://producer-internal-host:80/";

    @Override
    public String get() {

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response
                    = restTemplate.getForEntity(SERVICE_B_URL, String.class);
            return "Received response from Producer Service: " + response.getBody();
        } catch (Exception exc){
            exc.printStackTrace();
            return exc.getMessage();
        }
    }
}
