package com.xubao.client.connection.processor;

import com.xubao.comment.processorUtil.Processor;
import com.xubao.comment.proto.Connection;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xubao
 * @version 1.0
 * @since 2018/3/20
 */
public class HeartBeatResponseProcessor implements Processor<Connection.HeartbeatResponse> {
    private static Logger log = LoggerFactory.getLogger(HeartBeatResponseProcessor.class);
    @Override
    public void process(ChannelHandlerContext ctx, Connection.HeartbeatResponse msg) {
        log.debug("收到心跳返回");
    }
}
