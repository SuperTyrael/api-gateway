package org.BrownUniversity.mliang21;

import lombok.Data;

@Data
public class Config {
    private int port = 8888;

    /**
     * 服务唯一名称做服务发现
     */
    private String applicationName = "api-gateway";


    private String registryAddress = "127.0.0.1:8848";

    private String env = "dev";

    //Netty configuration
    private int eventLoopGroupBossNum = 1;

    private int eventLoopGroupWorkerNum = Runtime.getRuntime().availableProcessors();

    //Http报文大小限制
    private int maxContentLength = 64 * 1024 * 1024;

    //单异步或者双异步，默认单异步
    private boolean whenComplete = true;


}
