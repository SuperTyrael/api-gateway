package org.brown.mliang21.core.filter;

/**
 * 过滤器抽象接口
 */
public interface ProcessFilter<T> {

    /**
     * 过滤器是否执行的校验方法
     * 
     * @param t
     * @return
     * @throws Throwable
     */
    boolean check(T t) throws Throwable;

    /**
     * 过滤器执行的方法
     * 
     * @param t
     * @param args
     * @throws Throwable
     */
    void entry(T t, Object... args) throws Throwable;

    /**
     * 触发下一个过滤器执行
     * 
     * @param t
     * @param args
     * @throws Throwable
     */
    void executeNext(T t, Object... args) throws Throwable;

    /**
     * 参数传输的方法
     * 
     * @param t
     * @param args
     * @throws Throwable
     */
    void transformEntry(T t, Object... args) throws Throwable;

    /**
     * 过滤器初始化方法
     * 
     * @throws Exception
     */
    default void init() throws Exception {
    };

    /**
     * 过滤器销毁方法
     * 
     * @throws Exception
     */
    default void destroy() throws Exception {
    };

}
