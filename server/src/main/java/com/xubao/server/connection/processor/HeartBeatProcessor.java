package com.xubao.server.connection.processor;

import com.xubao.comment.message.MsgEncoding;
import com.xubao.comment.processorUtil.Processor;
import com.xubao.comment.proto.Connection;
import com.xubao.server.manager.ClientManager;
import com.xubao.server.pojo.ClientInfo;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author xubao
 * @Date 2018/2/18
 */
public class HeartBeatProcessor implements Processor<Connection.Heartbeat> {
    private static Logger log = LoggerFactory.getLogger(HeartBeatProcessor.class);

    @Override
    public void process(ChannelHandlerContext ctx, Connection.Heartbeat msg) {
        System.out.println("有心跳消息");
        ClientInfo client = ClientManager.getInstance().findClientByAddress(ctx.channel().remoteAddress().toString());
        if(client==null){
            log.info("未注册的玩家发来的心跳协议!");
            return;
        }

        client.setLastHeartBeatTime(System.currentTimeMillis());

        Connection.HeartbeatResponse.Builder builder = Connection.HeartbeatResponse.newBuilder();
        ctx.writeAndFlush(MsgEncoding.encode(builder.build()));
    }
}
