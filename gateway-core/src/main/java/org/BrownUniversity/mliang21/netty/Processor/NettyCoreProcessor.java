package org.BrownUniversity.mliang21.netty.Processor;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.BrownUniversity.mliang21.ConfigLoader;
import org.BrownUniversity.mliang21.common.enums.ResponseCode;
import org.BrownUniversity.mliang21.common.exception.BaseException;
import org.BrownUniversity.mliang21.common.exception.ConnectException;
import org.BrownUniversity.mliang21.common.exception.ResponseException;
import org.BrownUniversity.mliang21.context.GatewayContext;
import org.BrownUniversity.mliang21.netty.helper.ResponseHelper;
import org.BrownUniversity.mliang21.request.HttpRequestWrapper;
import org.BrownUniversity.mliang21.netty.helper.AsyncHttpHelper;
import org.BrownUniversity.mliang21.netty.helper.RequestHelper;
import org.BrownUniversity.mliang21.response.GatewayResponse;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;

import java.lang.ref.Reference;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

@Slf4j
public class NettyCoreProcessor implements NettyProcessor {

    @Override
    public void process(HttpRequestWrapper wrapper) {
        FullHttpRequest request = wrapper.getRequest();
        ChannelHandlerContext ctx = wrapper.getContext();

        try{
            GatewayContext gatewayContext = RequestHelper.doContext(request, ctx);
            route(gatewayContext);
        }catch (BaseException e){
            log.error("Process error {} {}", e.getCode().getCode(), e.getCode().getMessage());
            FullHttpResponse htttpResponse = ResponseHelper.getHttpResponse(e.getCode());
            doWriteAndRelease(ctx, request, htttpResponse);
        } catch (Throwable t){
            log.error("Process unknown error {}", t);
            ResponseHelper.getHttpResponse(ResponseCode.INTERNAL_ERROR);
        }
    }

    private  void doWriteAndRelease(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response){
        ctx.writeAndFlush(response)
                .addListener(ChannelFutureListener.CLOSE); //释放资源后关闭Channel
        ReferenceCountUtil.release(request);
    }

    /**
     * 路由函数做请求转发,tongg1
     * @param gatewayContext
     */
    public void route(GatewayContext gatewayContext){
        Request request = gatewayContext.getRequest().build();
        CompletableFuture<Response> future = AsyncHttpHelper.getInstance().executeRequest(request);

        boolean whenComplete = ConfigLoader.getConfig().isWhenComplete();

        if (whenComplete){
            //单异步
            future.whenComplete(((response, throwable) -> {
                complete(request, response, throwable, gatewayContext);
            }));
        }else {
            future.whenCompleteAsync((response, throwable) -> {
                complete(request,response,throwable,gatewayContext);
            });
        }
    }

    public void complete(Request request, Response response, Throwable throwable, GatewayContext gatewayContext){
        gatewayContext.releaseRequest();
        try{
            //有异常
            if (Objects.nonNull(throwable)){
                String url = request.getUrl();
                if (throwable instanceof TimeoutException){
                    log.warn("Complete time out {}", url);
                    gatewayContext.setThrowable(new ResponseException(ResponseCode.REQUEST_TIMEOUT));
                }else {
                    gatewayContext.setThrowable(new ConnectException(throwable, gatewayContext.getUniqueId(), request.getUrl(),ResponseCode.HTTP_RESPONSE_ERROR));
                }
            }else{
                gatewayContext.setResponse(GatewayResponse.buildGatewayResponse(response));
            }
        }catch (Throwable t){
            gatewayContext.setThrowable(new ResponseException(ResponseCode.INTERNAL_ERROR));
            log.error("Complete failed", t);
        }finally {
            gatewayContext.written();
            ResponseHelper.writeResponse(gatewayContext);
        }

    }
}
