package com.xubao.comment.processorUtil;

import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;

/**
 * @Author xubao
 * @Date 2018/2/17
 */
public interface Processor<P extends Message> {
    void process(ChannelHandlerContext ctx, P msg);
}
