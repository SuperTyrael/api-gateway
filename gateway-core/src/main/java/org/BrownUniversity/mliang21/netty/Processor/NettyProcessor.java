package org.BrownUniversity.mliang21.netty.Processor;

import org.BrownUniversity.mliang21.request.HttpRequestWrapper;

public interface NettyProcessor {

    void process(HttpRequestWrapper wrapper);
}
