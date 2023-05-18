package org.brown.mliang21.core.filter;

import java.lang.annotation.*;

/**
 * 过滤器注解类
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Filter {

    /**
     * 过滤器的唯一ID
     * 
     * @return
     */
    String id();

    /**
     * 过滤器的名称
     * 
     * @return
     */
    String name() default "";

    /**
     * 过滤器的类型
     * 
     * @return
     */
    ProcessorFilterType value();

    /**
     * 过滤器的执行顺序
     * 
     * @return
     */
    int order() default 0;
}
