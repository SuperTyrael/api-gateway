package org.brown.mliang21.gateway.client.support;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.brown.mliang21.common.config.ServiceDefinition;
import org.brown.mliang21.common.config.ServiceInstance;
import org.brown.mliang21.gateway.client.core.ApiProperties;
import org.brown.mliang21.gateway.register.center.api.RegisterCenter;

import java.util.ServiceLoader;

@Slf4j
public abstract class AbstractClientRegisterManager {
    @Getter
    private ApiProperties apiProperties;

    private RegisterCenter registerCenter;

    protected AbstractClientRegisterManager(ApiProperties apiProperties) {
        this.apiProperties = apiProperties;

        // 初始化注册中心对象
        ServiceLoader<RegisterCenter> serviceLoader = ServiceLoader.load(RegisterCenter.class);
        registerCenter = serviceLoader.findFirst().orElseThrow(() -> {
            log.error("not found RegisterCenter impl");
            return new RuntimeException("not found RegisterCenter impl");
        });
        registerCenter.init(apiProperties.getRegisterAddress(), apiProperties.getEnv());
    }

    protected void register(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance) {
        registerCenter.register(serviceDefinition, serviceInstance);
    }
}