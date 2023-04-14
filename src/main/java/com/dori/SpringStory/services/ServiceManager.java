package com.dori.SpringStory.services;

import com.dori.SpringStory.enums.ServiceType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Component
public class ServiceManager {

    private static final Map<ServiceType, BaseService<?>> services = new HashMap<>();

    public static void registerNewService(ServiceType type, BaseService<?> service){
        services.putIfAbsent(type,service);
    }

    public static BaseService<?> getService(ServiceType type){
        if(type == ServiceType.NotExist){
            throw new RuntimeException("This service type isn't supported!");
        }
        return services.get(type);
    }
}
