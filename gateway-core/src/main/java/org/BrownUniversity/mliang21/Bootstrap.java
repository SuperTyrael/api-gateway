package org.BrownUniversity.mliang21;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.BrownUniversity.mliang21.common.config.DynamicConfigManager;
import org.BrownUniversity.mliang21.common.config.ServiceDefinition;
import org.BrownUniversity.mliang21.common.config.ServiceInstance;
import org.BrownUniversity.mliang21.common.utils.NetUtils;
import org.BrownUniversity.mliang21.common.utils.TimeUtil;

import java.util.Map;
import java.util.Set;

import static org.BrownUniversity.mliang21.common.constants.BasicConst.COLON_SEPARATOR;

/**
 * API Gateway Bootstrap
 *
 */
@Slf4j
public class Bootstrap
{
    public static void main( String[] args )
    {
        //加载网关核心静态配置
        Config config = ConfigLoader.getInstance().load(args);
        System.out.println(config.getPort());
        //插件初始化

        //配置中心管理器初始化，连接配置中心，监听配置中心的新增、修改、删除
        final RegisterCenter registerCenter = registerAndSubscribe(config);

        //启动容器
        Container container = new Container(config);
        container.start();
        //连接注册中心，将注册中心的实例加载到本地

        //关机
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                registerCenter.deregister(buildGatewayServiceDefinition(config), buildGatewayServiceInstance(config));
            }
        });
    }

    private static RegisterCenter registerAndSubscribe(Config config) {
        RegisterCenter registerCenter = null;

        //构造网关的服务定义和服务实例
        ServiceDefinition serviceDefinition = buildGatewayServiceDefinition(config);
        ServiceInstance serviceInstance = buildGatewayServiceInstance(config);

        //注册网关
        registerCenter.register(serviceDefinition, serviceInstance);

        //订阅变更
        registerCenter.subscribeAllServices(new RegisterCenterListener() {
            @Override
            public void onChange(ServiceDefinition serviceDefinition, Set<ServiceInstance> serviceInstanceSet) {
                log.info("Refresh service and instances: {} {}", serviceDefinition.getUniqueId(), JSON.toJSON(serviceInstanceSet));
                DynamicConfigManager manager = DynamicConfigManager.getInstance();
                manager.addServiceInstance(serviceInstance.getUniqueId(), serviceInstanceSet);
            }
        });
        return registerCenter;
    }


    private static ServiceInstance buildGatewayServiceInstance(Config config) {
        String localIp = NetUtils.getLocalIp();
        int port = config.getPort();
        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setServiceInstanceId(localIp + COLON_SEPARATOR + port);
        serviceInstance.setIp(localIp);
        serviceInstance.setPort(port);
        serviceInstance.setRegisterTime(TimeUtil.currentTimeMillis());
        return serviceInstance;
    }

    private static ServiceDefinition buildGatewayServiceDefinition(Config config) {
        ServiceDefinition serviceDefinition = new ServiceDefinition();
        serviceDefinition.setInvokerMap(Map.of());
        serviceDefinition.setUniqueId(config.getApplicationName());
        serviceDefinition.setServiceId(config.getApplicationName());
        serviceDefinition.setEnvType(config.getEnv());
        return serviceDefinition;
    }
}
