package org.brown.mliang21.core.filter;

/**
 * 过滤器类型枚举类
 */
public enum ProcessorFilterType {

    PRE("PRE", "前置过滤器"),
    ROUTER("ROUTE", "中置过滤器"),
    POST("POST", "后置过滤器"),
    ERROR("ERROR", "异常过滤器");

    private final String code;
    private final String message;

    ProcessorFilterType(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
