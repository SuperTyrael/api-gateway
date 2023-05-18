package org.brown.mliang21.core.filter;

import org.brown.mliang21.core.context.GatewayContext;
import org.brown.mliang21.core.helper.ResponseHelper;

/**
 * @PROJECT_NAME: api-gateway
 * @DESCRIPTION: 抽象的链表形式过滤器
 * @USER: WuYang
 * @DATE: 2023/2/18 23:41
 */
public abstract class AbstractLinkedProcessorFilter<T> implements ProcessFilter<GatewayContext> {
    /**
     * 下一个过滤器的引用
     */
    protected AbstractLinkedProcessorFilter<T> next = null;

    @Override
    public void executeNext(GatewayContext ctx, Object... args) throws Throwable {
        // 上下文生命周期
        if (ctx.isTerminated()) {
            return;
        }
        if (ctx.isWritten()) {
            ResponseHelper.writeResponse(ctx);
        }
        if (next != null) {
            if (!next.check(ctx)) {
                next.executeNext(ctx, args);
            } else {
                next.transformEntry(ctx, args);
            }
        } else {
            ctx.terminated();
            return;
        }
    }

    @Override
    public void transformEntry(GatewayContext ctx, Object... args) throws Throwable {
        // 子类调用。真正执行下一个过滤器的方法
        entry(ctx, args);
    }

    public AbstractLinkedProcessorFilter<T> getNext() {
        return next;
    }

    public void setNext(AbstractLinkedProcessorFilter<T> next) {
        this.next = next;
    }
}
