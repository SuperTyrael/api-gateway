package org.BrownUniversity.mliang21;

import org.BrownUniversity.mliang21.common.config.ServiceDefinition;
import org.BrownUniversity.mliang21.common.config.ServiceInstance;

import java.util.Set;

public interface RegisterCenterListener {
    void onChange(ServiceDefinition serviceDefinition, Set<ServiceInstance> serviceInstanceSet);
}
