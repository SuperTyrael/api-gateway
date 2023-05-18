package org.brown.mliang21.core;

import lombok.extern.slf4j.Slf4j;
import org.brown.mliang21.core.netty.NettyHttpClient;
import org.brown.mliang21.core.netty.NettyHttpServer;
import org.brown.mliang21.core.netty.processor.NettyCoreProcessor;
import org.brown.mliang21.core.netty.processor.NettyProcessor;

@Slf4j
public class Container implements LifeCycle {
    private final Config config;

    private NettyHttpServer nettyHttpServer;

    private NettyHttpClient nettyHttpClient;

    private NettyProcessor nettyProcessor;

    public Container(Config config) {
        this.config = config;
        init();
    }

    @Override
    public void init() {
        this.nettyProcessor = new NettyCoreProcessor();

        this.nettyHttpServer = new NettyHttpServer(config, nettyProcessor);

        this.nettyHttpClient = new NettyHttpClient(config,
                nettyHttpServer.getEventLoopGroupWoker());
    }

    @Override
    public void start() {
        nettyHttpServer.start();
        ;
        nettyHttpClient.start();
        log.info("api gateway started!");
    }

    @Override
    public void shutdown() {
        nettyHttpServer.shutdown();
        nettyHttpClient.shutdown();
    }
}
