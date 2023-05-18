package org.brown.mliang21.core.netty.processor;

import org.brown.mliang21.core.context.HttpRequestWrapper;

public interface NettyProcessor {

    void process(HttpRequestWrapper wrapper);
}
