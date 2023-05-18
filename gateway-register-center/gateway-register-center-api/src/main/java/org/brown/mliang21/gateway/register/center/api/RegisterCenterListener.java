package org.brown.mliang21.gateway.register.center.api;

import org.brown.mliang21.common.config.ServiceDefinition;
import org.brown.mliang21.common.config.ServiceInstance;

import java.util.Set;

public interface RegisterCenterListener {

    void onChange(ServiceDefinition serviceDefinition,
            Set<ServiceInstance> serviceInstanceSet);
}
