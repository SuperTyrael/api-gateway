package org.BrownUniversity.mliang21.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpServerChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.BrownUniversity.mliang21.Config;
import org.BrownUniversity.mliang21.LifeCycle;
import org.BrownUniversity.mliang21.common.utils.RemotingUtil;
import org.BrownUniversity.mliang21.netty.Processor.NettyProcessor;

import java.net.InetSocketAddress;

@Slf4j
public class NettyHttpServer implements LifeCycle {
    private final Config config;
    private ServerBootstrap serverBootstrap;
    private EventLoopGroup eventLoopGroupBoss;
    @Getter
    private EventLoopGroup eventLoopGroupWorker;
    private final NettyProcessor nettyProcessor;

    public NettyHttpServer(Config config, NettyProcessor nettyProcessor){
        this.config = config;
        this.nettyProcessor = nettyProcessor;
        init();
    }

    @Override
    public void init() {
        if (useEpoll()){
            this.serverBootstrap = new ServerBootstrap();
            this.eventLoopGroupBoss = new EpollEventLoopGroup(config.getEventLoopGroupBossNum(), new DefaultThreadFactory("netty-boss-nio"));
            this.eventLoopGroupWorker = new EpollEventLoopGroup(config.getEventLoopGroupWorkerNum(), new DefaultThreadFactory("netty-worker-nio"));
        }else{
            this.serverBootstrap = new ServerBootstrap();
            this.eventLoopGroupBoss = new NioEventLoopGroup(config.getEventLoopGroupBossNum(), new DefaultThreadFactory("netty-boss-nio"));
            this.eventLoopGroupWorker = new NioEventLoopGroup(config.getEventLoopGroupWorkerNum(), new DefaultThreadFactory("netty-worker-nio"));
        }
    }

    /**
     * 使用Epoll相关API
     */
    public boolean useEpoll(){
        return RemotingUtil.isLinuxPlatform() && Epoll.isAvailable();
    }

    @Override
    public void start() {
        this.serverBootstrap
                .group(eventLoopGroupBoss, eventLoopGroupWorker)
                .channel(useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(config.getPort()))
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception{
                        ch.pipeline().addLast(
                                new HttpServerCodec(), //http encoding decoding
                                new HttpObjectAggregator(config.getMaxContentLength()),
                                new NettyServerConnectManagerHandler(),
                                new NettyHttpServerHandler(nettyProcessor)
                        );
                    }
                });
        try {
            this.serverBootstrap.bind().sync();
            log.info("server started on port {}", config.getPort());
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void shutdown() {
        if (eventLoopGroupBoss != null){
            eventLoopGroupBoss.shutdownGracefully();
        }
        if(eventLoopGroupWorker != null){
            eventLoopGroupWorker.shutdownGracefully();
        }
    }
}
