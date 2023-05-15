package org.BrownUniversity.mliang21.request;

import org.asynchttpclient.Request;
import org.asynchttpclient.cookie.Cookie;

/**
 * 可修改的request参数操作接口
 */
public interface IGatewayRequest {

    /**
     * 修改目标服务主机地址
     * @param host
     */
    void setModifyHost(String host);

    /**
     * 获取目标服务主机
     * @return
     */
    String getModifyHost();

    /**
     * 设置目标服务路径
     * @param path
     */
    void setModifyPath(String path);

    /**
     * 获取目标服务路径
     *
     */
    String getModifyPath();

    /**
     * add request header
     * @param name
     * @param value
     */
    void addHeader(CharSequence name, String value);

    /**
     * set request header
     * @param name
     * @param value
     */
    void setHeader(CharSequence name, String value);

    /**
     * add Get param
     * @param name
     * @param value
     */
    void addQueryParam(String name, String value);

    /**
     * add form param
     * @param name
     * @param value
     */
    void addFormParam(String name, String value);

    /**
     * add or replace cookie
     * @param cookie
     */
    void addOrReplaceCookie(Cookie cookie);

    /**
     * set request timeout
     * @param requestTimeout
     */
    void setRequestTimeout(int requestTimeout);

    /**
     * Get the final request path, including reqeust params
     * e.g: http://localhost:8081/API/admain?name=1
     *
     * @return
     */
    String getFinalUrl();

    /**
     *
     * @return
     */
    Request build();
}
