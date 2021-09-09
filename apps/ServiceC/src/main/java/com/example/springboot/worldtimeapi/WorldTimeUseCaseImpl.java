package com.example.springboot.worldtimeapi;

import com.example.springboot.worldtimeapi.adapters.Adapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorldTimeUseCaseImpl implements WorldTimeUseCase {

    @Autowired
    private Adapter adapter;

    @Override
    public String getDataFromWorldTime() {
        return adapter.get();
    }
}
