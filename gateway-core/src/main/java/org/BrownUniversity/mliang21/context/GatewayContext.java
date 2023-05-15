package org.BrownUniversity.mliang21.context;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import org.BrownUniversity.mliang21.common.rule.Rule;
import org.BrownUniversity.mliang21.common.utils.AssertUtil;
import org.BrownUniversity.mliang21.request.GatewayRequest;
import org.BrownUniversity.mliang21.response.GatewayResponse;

public class GatewayContext extends BaseContext{

    public GatewayRequest request;

    public GatewayResponse response;

    public Rule rule;

    public GatewayContext(String protocol, ChannelHandlerContext nettyCtx, boolean keepAlive, GatewayRequest request, Rule rule) {
        super(protocol, nettyCtx, keepAlive);
        this.request = request;
        this.rule = rule;
    }

    public static class Builder{
        private String protocol;
        private ChannelHandlerContext nettyCtx;
        private GatewayRequest request;
        private Rule rule;
        private boolean keepAlive;

        public Builder(){

        }

        public Builder setProtocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder setNettyCtx(ChannelHandlerContext nettyCtx) {
            this.nettyCtx = nettyCtx;
            return this;
        }

        public Builder setRequest(GatewayRequest request) {
            this.request = request;
            return this;
        }

        public Builder setRule(Rule rule) {
            this.rule = rule;
            return this;
        }

        public Builder setKeepAlive(boolean keepAlive) {
            this.keepAlive = keepAlive;
            return this;
        }

        public GatewayContext build(){
            AssertUtil.notNull(protocol, "protocol cannot be NULL");
            AssertUtil.notNull(nettyCtx, "netty cannot be NULL");
            AssertUtil.notNull(rule, "rule cannot be NULL");
            AssertUtil.notNull(request, "request cannot be NULL");
            return new GatewayContext(protocol, nettyCtx, keepAlive, request, rule);
        }
    }

    /**
     * 获取必要上下文参数
     */
    public <T> T getRequiredAttributes(String key){
        T value = getRequiredAttributes(key);
        AssertUtil.notNull(value, "Lack of necessary parameters");
        return  value;
    }

    /**
     * 获取指定key的上下文参数，如果没有返回默认值
     */
    public <T> T getRequiredAttributes(String key, T defaultValue){
        return (T) attributes.getOrDefault(key, defaultValue);
    }

    /**
     * 获取指定的过滤器信息
     */
    public Rule.FilterConfig getFilterConfig(String filterId){
        return rule.getFilterConfig(filterId);
    }

    /**
     * get request service id
     */
    public String getUniqueId(){
        return request.getUniqueId();
    }

    /**
     * 重写父类，释放资源
     */
    public boolean releaseRequest(){
        if (requestReleased.compareAndSet(false,true)){
            ReferenceCountUtil.release(request.getFullHttpRequest());
        }
        return true;
    }

    /**
     * 获取原始请求对象的方法
     * @return
     */
    public GatewayRequest getOriginRequest(){
        return request;
    }

    @Override
    public GatewayRequest getRequest() {
        return request;
    }

    public void setRequest(GatewayRequest request) {
        this.request = request;
    }

    @Override
    public GatewayResponse getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = (GatewayResponse) response;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }
}
