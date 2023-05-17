package org.BrownUniversity.mliang21;

import lombok.extern.slf4j.Slf4j;
import org.BrownUniversity.mliang21.netty.NettyHttpClient;
import org.BrownUniversity.mliang21.netty.NettyHttpServer;
import org.BrownUniversity.mliang21.netty.Processor.NettyCoreProcessor;
import org.BrownUniversity.mliang21.netty.Processor.NettyProcessor;

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
        this.nettyHttpClient = new NettyHttpClient(config, nettyHttpServer.getEventLoopGroupWorker());

    }

    @Override
    public void start() {
        nettyHttpServer.start();
        nettyHttpClient.start();
        log.info("API Gateway Started!");
    }

    @Override
    public void shutdown() {
        nettyHttpServer.shutdown();
        nettyHttpClient.shutdown();
        log.info("API Gateway shutdown!");
    }
}
