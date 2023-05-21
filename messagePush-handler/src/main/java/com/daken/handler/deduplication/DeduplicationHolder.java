package com.daken.handler.deduplication;

import com.daken.handler.deduplication.build.Builder;
import com.daken.handler.deduplication.service.DeduplicationService;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class DeduplicationHolder {
    private final HashMap<Integer, DeduplicationService> serviceHashMap = new HashMap<>();
    private final HashMap<Integer, Builder> builderHashMap = new HashMap<>();

    public Builder getBuilderHashMap(Integer key) {
        return builderHashMap.get(key);
    }

    public DeduplicationService getServiceHashMap(Integer key) {
        return serviceHashMap.get(key);
    }
    public void putBuilder(Integer key, Builder builder){
        builderHashMap.put(key, builder);
    }
    public void putService(Integer key, DeduplicationService service){
        serviceHashMap.put(key, service);
    }

}
