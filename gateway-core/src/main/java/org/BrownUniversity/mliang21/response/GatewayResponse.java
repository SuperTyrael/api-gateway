package org.BrownUniversity.mliang21.response;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.handler.codec.http.*;
import lombok.Data;
import org.BrownUniversity.mliang21.common.enums.ResponseCode;
import org.BrownUniversity.mliang21.common.utils.JSONUtil;
import org.asynchttpclient.Response;

@Data
public class GatewayResponse {
    private HttpHeaders responseHeaders = new DefaultHttpHeaders();

    /**
     * 额外响应头
     */
    private HttpHeaders extraResponseHeaders = new DefaultHttpHeaders();

    /**
     * 相应内容
     */
    private String content;

    /**
     * 返回响应状态码
     */
    private HttpResponseStatus httpResponseStatus;

    /**
     * 异步返回对象
     */
    private Response futureResponse;

    public GatewayResponse(){

    }

    /**
     * 设置响应头信息
     */
    public void putHeader(CharSequence key, CharSequence value){
        responseHeaders.add(key,value);
    }


    /**
     * 构建异步的响应对象
     * @param futureResponse
     * @return
     */
    public static GatewayResponse buildGatewayResponse(Response futureResponse){
        GatewayResponse response = new GatewayResponse();
        response.setFutureResponse(futureResponse);
        response.setHttpResponseStatus(HttpResponseStatus.valueOf(futureResponse.getStatusCode()));
        return response;
    }

    /**
     * 返回Json类型响应信息,失败时候调用，方便调试
     */
    public static GatewayResponse buildGatewayResponse(ResponseCode code, Object...arg){
        ObjectNode objectNode = JSONUtil.createObjectNode();
        objectNode.put(JSONUtil.STATUS, code.getStatus().code());
        objectNode.put(JSONUtil.CODE, code.getCode());
        objectNode.put(JSONUtil.MESSAGE, code.getMessage());

        GatewayResponse response = new GatewayResponse();
        response.setHttpResponseStatus(code.getStatus());
        response.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON+";charset=utf-8");
        response.setContent(JSONUtil.toJSONString(objectNode));
        return response;
    }

    /**
     * 返回Json类型响应信息,成功时候调用，方便调试
     */
    public static GatewayResponse buildGatewayResponse(Object data){
        ObjectNode objectNode = JSONUtil.createObjectNode();
        objectNode.put(JSONUtil.STATUS, ResponseCode.SUCCESS.getStatus().code());
        objectNode.put(JSONUtil.STATUS, ResponseCode.SUCCESS.getCode());
        objectNode.putPOJO(JSONUtil.DATA, data);

        GatewayResponse response = new GatewayResponse();
        response.setHttpResponseStatus(ResponseCode.SUCCESS.getStatus());
        response.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON+";charset=utf-8");
        response.setContent(JSONUtil.toJSONString(objectNode));
        return response;
    }
}
