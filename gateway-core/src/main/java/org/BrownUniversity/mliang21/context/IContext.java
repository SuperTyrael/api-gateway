package org.BrownUniversity.mliang21.context;

import io.netty.channel.ChannelHandlerContext;

import java.util.function.Consumer;

public interface IContext {
    /**
     * 上下文生命周期，运行中状态
     */
    int Running = 0;

    /**
     * 运行过程中发生错误，对其进行标记，请求已经结束，需要返回客户端
     */
    int Written = 1;

    /**
     * 标记写回成功，防止并发情况下的多次写回
     */
    int Completed = 2;

    /**
     * 网关请求结束
     */
    int Terminated = -1;

    /**
     * 设置上下文状态为运行中
     */
    void running();

    /**
     * 设置上下文状态为标记写回
     */
    void written();

    /**
     * 设置上下文宅男改为写回成功
     */
    void completed();

    /**
     * 设置上下文状态为请求结束
     */
    void terminated();

    boolean isRunning();
    boolean isWritten();
    boolean isCompleted();
    boolean isTerminated();

    /**
     * 获取协议
     * @return
     */
    String getProtocol();

    /**
     * 获取请求对象
     * @return
     */
    Object getRequest();

    Object getResponse();

    void setResponse();

    Throwable getThrowable();

    void setThrowable(Throwable throwable);

    /**
     * Get Netty request ctx
     * @return
     */
    ChannelHandlerContext getNettyCtx();

    /**
     *
     * @return
     */
    boolean isKeepAlive();

    /**
     * Release request
     * @return
     */
    boolean releaseRequest();

    /**
     * 设置写回接受回调函数
     * @param consumer
     */
    void setCompletedCallBack(Consumer<IContext> consumer);

    /**
     * 执行写回接收回调函数
     */
    void invokeCompletedCallBack();
}
