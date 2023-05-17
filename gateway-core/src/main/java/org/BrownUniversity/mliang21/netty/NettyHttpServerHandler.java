package org.BrownUniversity.mliang21.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import org.BrownUniversity.mliang21.request.HttpRequestWrapper;
import org.BrownUniversity.mliang21.netty.Processor.NettyProcessor;

public class NettyHttpServerHandler extends ChannelInboundHandlerAdapter {
    private final NettyProcessor nettyProcessor;

    public NettyHttpServerHandler(NettyProcessor nettyProcessor) {
        this.nettyProcessor = nettyProcessor;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
        FullHttpRequest request = (FullHttpRequest) msg;

        HttpRequestWrapper wrapper = new HttpRequestWrapper();
        wrapper.setRequest(request);
        wrapper.setContext(ctx);

        nettyProcessor.process(wrapper);
    }
}
