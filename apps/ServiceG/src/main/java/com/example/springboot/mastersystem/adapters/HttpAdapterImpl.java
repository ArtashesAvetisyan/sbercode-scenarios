package com.example.springboot.mastersystem.adapters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class HttpAdapterImpl implements Adapter {

    @Value("${target.url}")
    private String targetUrl;

    Logger logger = LoggerFactory.getLogger(HttpAdapterImpl.class);

    @Override
    public String get() {

        System.out.println(targetUrl);

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response
                    = restTemplate.getForEntity(targetUrl, String.class);
            String rs = "Received response from master system (" + targetUrl + "): " +
                    response.getBody();
                    logger.info(rs);
            return rs;
        } catch (Exception exc){
            exc.printStackTrace();
            return exc.getMessage();
        }
    }
}
