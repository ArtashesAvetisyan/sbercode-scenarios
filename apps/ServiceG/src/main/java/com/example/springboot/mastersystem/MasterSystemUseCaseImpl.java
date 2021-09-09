package com.example.springboot.mastersystem;

import com.example.springboot.mastersystem.adapters.Adapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MasterSystemUseCaseImpl implements MasterSystemUseCase {

    @Autowired
    private Adapter adapter;

    @Override
    public String getData() {
        return adapter.get();
    }
}
