package com.xubao.client.connection.processor;

import com.xubao.comment.processorUtil.Processor;
import com.xubao.comment.proto.Connection;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author xubao
 * @version 1.0
 * @since 2018/3/20
 */
public class HeartBeatResponseProcessor implements Processor<Connection.HeartbeatResponse> {
    @Override
    public void process(ChannelHandlerContext ctx, Connection.HeartbeatResponse msg) {
        System.out.println("收到心跳返回");
    }
}
