package com.example.springboot.serviceb;

import com.example.springboot.serviceb.adapters.Adapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceBUseCaseImpl implements ServiceBUseCase {

    @Autowired
    private Adapter adapter;

    @Override
    public String getDataFromServiceB() {
        return adapter.get();
    }
}
