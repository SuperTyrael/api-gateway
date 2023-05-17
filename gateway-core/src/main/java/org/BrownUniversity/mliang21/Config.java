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

    //	Http Async 参数选项：

    //	连接超时时间
    private int httpConnectTimeout = 30 * 1000;

    //	请求超时时间
    private int httpRequestTimeout = 30 * 1000;

    //	客户端请求重试次数
    private int httpMaxRequestRetry = 2;

    //	客户端请求最大连接数
    private int httpMaxConnections = 10000;

    //	客户端每个地址支持的最大连接数
    private int httpConnectionsPerHost = 8000;

    //	客户端空闲连接超时时间, 默认60秒
    private int httpPooledConnectionIdleTimeout = 60 * 1000;


}
