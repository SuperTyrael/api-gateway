package org.BrownUniversity.mliang21.netty;

import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.EventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.BrownUniversity.mliang21.Config;
import org.BrownUniversity.mliang21.LifeCycle;
import org.BrownUniversity.mliang21.netty.helper.AsyncHttpHelper;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;

@Slf4j
public class NettyHttpClient implements LifeCycle {
    private final Config config;

    private final EventLoopGroup eventLoopGroupWorker;

    private AsyncHttpClient asyncHttpClient;

    public NettyHttpClient(Config config, EventLoopGroup eventLoopGroupWorker) {
        this.config = config;
        this.eventLoopGroupWorker = eventLoopGroupWorker;
        init();
    }

    @Override
    public void init() {
        DefaultAsyncHttpClientConfig.Builder builder = new DefaultAsyncHttpClientConfig.Builder()
                .setEventLoopGroup(eventLoopGroupWorker)
                .setConnectTimeout(config.getHttpConnectTimeout())
                .setRequestTimeout(config.getHttpRequestTimeout())
                .setMaxRedirects(config.getHttpMaxRequestRetry())
                .setAllocator(PooledByteBufAllocator.DEFAULT) //池化的ByteBuffer分配器,提升性能
                .setCompressionEnforced(true)
                .setMaxConnections(config.getHttpMaxConnections())
                .setMaxConnectionsPerHost(config.getHttpConnectionsPerHost())
                .setPooledConnectionIdleTimeout(config.getHttpPooledConnectionIdleTimeout());
        this.asyncHttpClient = new DefaultAsyncHttpClient(builder.build());
    }

    @Override
    public void start() {
        AsyncHttpHelper.getInstance().initialized(asyncHttpClient);
    }

    @Override
    public void shutdown() {
        if (asyncHttpClient != null){
            try {
                this.asyncHttpClient.close();
            }catch (Exception e){
                log.error("NettyHttpClient shutdown error", e);
            }
        }
    }
}
