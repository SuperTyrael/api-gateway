package org.BrownUniversity.mliang21.request;

import com.alibaba.fastjson.JSONPath;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import lombok.Getter;
import org.BrownUniversity.mliang21.common.constants.BasicConst;
import org.BrownUniversity.mliang21.common.utils.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;

import java.nio.charset.Charset;
import java.util.*;

public class GatewayRequest implements IGatewayRequest {

    @Getter
    private final String uniqueId;

    /**
     * Start time access the gateway
     */
    @Getter
    private final long beginTime;

//    @Getter
//    private final long endTime;

    /**
     * Encoding charset
     */
    private final Charset charset;

    @Getter
    private final String clientIp;

    @Getter
    private final String host;

    /**
     * Server path
     * e.g: /xxx/xx/xx
     */
    @Getter
    private final String path;

    /**
     * Unified resource index
     * e.g.: /xxx/xx/xx?attr1=1&attr=2
     */
    private final String uri;

    /**
     * POST/GET/PUT
     */
    @Getter
    private final HttpMethod method;

    /**
     * Request contentType
     */
    @Getter
    private final String contentType;

    @Getter
    private final HttpHeaders headers;

    /**
     * Parameter decoder
     */
    @Getter
    private final QueryStringDecoder queryStringDecoder;

    /**
     * whole http request
     */
    @Getter
    private final FullHttpRequest fullHttpRequest;

    /**
     * request body
     */
    private String body;

    /**
     * get cookies from body
     */
    private Map<String, io.netty.handler.codec.http.cookie.Cookie> cookieMap;

    /**
     *  Post request parms
     */
    private Map<String, List<String>> postParameters;

    /**
     * 可修改的Scheme，默认为HTTP://
     */
    private String modifyScheme;

    /**
     * 可修改的主机名
     */
    private String modifyHost;

    /**
     * 可修改的请求路径
     */
    private String modifyPath;

    /**
     * 构建下游HTTP请求的构建器
     */
    private final RequestBuilder requestBuilder;

//    public GatewayRequest(String uniqueId,long endTime, Charset charset, String clientIp, String host, String path, String uri, String contentType, QueryStringDecoder queryStringDecoder, FullHttpRequest fullHttpRequest, HttpMethod method, HttpHeaders headers) {
//        this.uniqueId = uniqueId;
//        this.method = method;
//        this.headers = headers;
//        this.beginTime = TimeUtil.currentTimeMillis();
//        this.endTime = endTime;
//        this.charset = charset;
//        this.clientIp = clientIp;
//        this.contentType = contentType;
//        //初始化防止报错
//        this.queryStringDecoder = new QueryStringDecoder(uri,charset);
//        this.fullHttpRequest = fullHttpRequest;
//        this.requestBuilder = new RequestBuilder();
//        this.host = host;
//        this.path = queryStringDecoder.path();
//        this.uri = uri;
//
//        this.modifyHost = host;
//        this.modifyPath = path;
//        this.modifyScheme = BasicConst.HTTP_PREFIX_SEPARATOR;
//        this.requestBuilder.setMethod(getMethod().name());
//        this.requestBuilder.setHeaders(getHeaders());
//        this.requestBuilder.setQueryParams(queryStringDecoder.parameters());
//
//        ByteBuf contentBuffer = fullHttpRequest.content();
//        if(Objects.nonNull(contentBuffer)){
//            this.requestBuilder.setBody(contentBuffer.nioBuffer());
//        }
//    }

    public GatewayRequest(String uniqueId, Charset charset, String clientIp, String host, String uri, HttpMethod method, String contentType, HttpHeaders headers, FullHttpRequest fullHttpRequest) {
        this.uniqueId = uniqueId;
        this.method = method;
        this.headers = headers;
        this.beginTime = TimeUtil.currentTimeMillis();
//        this.endTime = endTime;
        this.charset = charset;
        this.clientIp = clientIp;
        this.contentType = contentType;
        //初始化防止报错
        this.queryStringDecoder = new QueryStringDecoder(uri,charset);
        this.fullHttpRequest = fullHttpRequest;
        this.requestBuilder = new RequestBuilder();
        this.host = host;
        this.path = queryStringDecoder.path();
        this.uri = uri;

        this.modifyHost = host;
        this.modifyPath = path;
        this.modifyScheme = BasicConst.HTTP_PREFIX_SEPARATOR;
        this.requestBuilder.setMethod(getMethod().name());
        this.requestBuilder.setHeaders(getHeaders());
        this.requestBuilder.setQueryParams(queryStringDecoder.parameters());

        ByteBuf contentBuffer = fullHttpRequest.content();
        if(Objects.nonNull(contentBuffer)){
            this.requestBuilder.setBody(contentBuffer.nioBuffer());
        }
    }

    /**
     * Get the body of request
     * @return
     */
    public String getBody(){
        if (StringUtils.isEmpty(body)){
            body = fullHttpRequest.content().toString();
        }
        return body;
    }

    public io.netty.handler.codec.http.cookie.Cookie getCookie(String name){
        if(cookieMap == null){
            cookieMap = new HashMap<String, io.netty.handler.codec.http.cookie.Cookie>();
            String cookieStr = getHeaders().get(HttpHeaderNames.COOKIE);
            Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieStr);
            for (io.netty.handler.codec.http.cookie.Cookie cookie : cookies){
                cookieMap.put(cookie.name(),cookie);
            }
        }
        return cookieMap.get(name);
    }


    public List<String> getQueryParametersMultiple(String name){
        return queryStringDecoder.parameters().get(name);
    }

    public List<String> getPostParameterMultiple(String name){
        String body = getBody();
        if (isFormPost()){
            if (postParameters == null){
                QueryStringDecoder paramDecoder = new QueryStringDecoder(body,false);
                postParameters = paramDecoder.parameters();
            }

            if (postParameters == null || postParameters.isEmpty()){
                return null;
            }else {
                return postParameters.get(name);
            }
        }else if (isJsonPost()){
            return Lists.newArrayList(JSONPath.read(body, name).toString());
        }
        return null;
    }

    public boolean isFormPost(){
        return HttpMethod.POST.equals(method) && (contentType.startsWith(HttpHeaderValues.FORM_DATA.toString()) || contentType.startsWith(HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString()));
    }

    public boolean isJsonPost(){
        return HttpMethod.POST.equals(method) && (contentType.startsWith(HttpHeaderValues.APPLICATION_JSON.toString()));
    }

    @Override
    public void setModifyHost(String host) {
        this.modifyHost = host;
    }

    @Override
    public String getModifyHost() {
        return modifyHost;
    }

    @Override
    public void setModifyPath(String path) {
        this.modifyPath = path;
    }

    @Override
    public String getModifyPath() {
        return modifyPath;
    }

    @Override
    public void addHeader(CharSequence name, String value) {
        requestBuilder.addHeader(name,value);
    }

    @Override
    public void setHeader(CharSequence name, String value) {
        requestBuilder.setHeader(name,value);
    }

    @Override
    public void addQueryParam(String name, String value) {
        requestBuilder.addQueryParam(name,value);
    }

    @Override
    public void addFormParam(String name, String value) {
        if (isFormPost()){
            requestBuilder.addFormParam(name, value);
        }
    }

    @Override
    public void addOrReplaceCookie(org.asynchttpclient.cookie.Cookie cookie) {
        requestBuilder.addOrReplaceCookie(cookie);
    }

    @Override
    public void setRequestTimeout(int requestTimeout) {
        requestBuilder.setRequestTimeout(requestTimeout);
    }

    @Override
    public String getFinalUrl() {
        return modifyScheme + modifyHost + modifyPath;
    }

    @Override
    public Request build() {
        requestBuilder.setUrl(getFinalUrl());
        return requestBuilder.build();
    }
}
