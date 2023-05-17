package org.BrownUniversity.mliang21.register.center.nacos;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingMaintainFactory;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.Service;
import com.alibaba.nacos.api.naming.pojo.ServiceInfo;
import com.alibaba.nacos.client.naming.NacosNamingService;
import com.alibaba.nacos.common.executor.NameThreadFactory;
import com.alibaba.nacos.common.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.BrownUniversity.mliang21.RegisterCenter;
import org.BrownUniversity.mliang21.RegisterCenterListener;
import org.BrownUniversity.mliang21.common.config.ServiceDefinition;
import org.BrownUniversity.mliang21.common.config.ServiceInstance;
import org.BrownUniversity.mliang21.common.constants.GatewayConst;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class NacosRegisterCenter implements RegisterCenter {
    private String registerAddress;

    private String env;

    //维护服务实例信息
    private NamingService namingService;

    //维护服务定义信息
    private NamingMaintainService namingMaintainService;

    //监听器列表
    private List<RegisterCenterListener> registerCenterListenerList;

    @Override
    public void init(String registerAddress, String env) {
        this.registerAddress = registerAddress;
        this.env = env;

        try {
            this.namingMaintainService = NamingMaintainFactory.createMaintainService(registerAddress);
            this.namingService = NamingFactory.createNamingService(registerAddress);
        } catch (NacosException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void register(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance) {
        try{
            //构造Nacos实例信息
            Instance nacosInstance = new Instance();
            nacosInstance.setInstanceId(serviceInstance.getServiceInstanceId());
            nacosInstance.setPort(serviceInstance.getPort());
            nacosInstance.setIp(serviceInstance.getIp());
            nacosInstance.setMetadata(Map.of(GatewayConst.META_DATA_KEY,JSON.toJSONString(serviceInstance)));

            //注册
            namingService.registerInstance(serviceDefinition.getServiceId(), env, nacosInstance);

            //更新服务定义
            namingMaintainService.updateService(serviceDefinition.getServiceId(), env, 0, Map.of(GatewayConst.META_DATA_KEY, JSON.toJSONString(serviceInstance)));

            log.info("Register {} {}", serviceDefinition, serviceInstance);
        }catch (NacosException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deregister(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance) {
        try{
            namingService.registerInstance(serviceDefinition.getServiceId(), env, serviceInstance.getIp(), serviceInstance.getPort());
        }catch (NacosException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void subscribeAllServices(RegisterCenterListener registerCenterListener) {
        registerCenterListenerList.add(registerCenterListener);
        doSubscribeAllServices();

        //可能有新任服务加入，需要一个定时任务来检查
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1, new NameThreadFactory("doSubscribeAllServices"));
        scheduledThreadPool.scheduleWithFixedDelay(()->doSubscribeAllServices(), 10, 10, TimeUnit.SECONDS);
    }

    private void doSubscribeAllServices() {
        try{
            //已经订阅的服务
            Set<String> subscribeService = namingService.getSubscribeServices()
                    .stream()
                    .map(ServiceInfo::getName)
                    .collect(Collectors.toSet());

            int pageNo = 1;
            int pageSize = 100;

            //nacos时间监听器
            EventListener eventListener = new NacosRegisterListener();

            //分页从Nacos拿到服务列表
            List<String> serviceList = namingService.getServicesOfServer(pageNo, pageSize, env).getData();

            while (CollectionUtils.isNotEmpty(serviceList)){
                log.info("service list size {}", serviceList.size());

                for (String service : serviceList){
                    if (subscribeService.contains(service)){
                        continue;
                    }

                    namingService.subscribe(service,eventListener);
                    log.info("Subscribe {} {}", service,env);
                }

                serviceList = namingService.getServicesOfServer(++pageNo, pageSize, env).getData();
            }
        }catch (NacosException e){
            throw new RuntimeException(e);
        }


    }
    public class NacosRegisterListener implements com.alibaba.nacos.api.naming.listener.EventListener{

        @Override
        public void onEvent(Event event) {
            if (event instanceof NamingEvent){
                NamingEvent namingEvent = (NamingEvent) event;
                String serviceName = namingEvent.getServiceName();


                try {
                    // 根据名字获取服务定义的信息
                    Service service = namingMaintainService.queryService(serviceName, env);
                    //反序列化为自定义服务定义
                    ServiceDefinition serviceDefinition = JSON.parseObject(service.getMetadata().get(GatewayConst.META_DATA_KEY), ServiceDefinition.class);

                    //获取服务实力信息
                    List<Instance> allInstances = namingService.getAllInstances(serviceName, env);
                    Set<ServiceInstance> set = new HashSet<>();

                    for (Instance instance : allInstances){
                        //反序列化为自定义服务实例
                        ServiceInstance serviceInstance = JSON.parseObject(instance.getMetadata().get(GatewayConst.META_DATA_KEY), ServiceInstance.class);
                        set.add(serviceInstance);
                    }

                    registerCenterListenerList.stream()
                            .forEach(l -> l.onChange(serviceDefinition, set));
                } catch (NacosException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
