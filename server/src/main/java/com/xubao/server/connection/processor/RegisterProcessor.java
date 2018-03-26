package com.xubao.server.connection.processor;

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
public class RegisterProcessor implements Processor<Connection.Register> {
    private static Logger log = LoggerFactory.getLogger(RegisterProcessor.class);
    @Override
    public void process(ChannelHandlerContext ctx, Connection.Register msg) {
        log.debug("接收到注册消息");

        ClientInfo client = new ClientInfo();
        client.setAddress(ctx.channel().remoteAddress().toString());
        client.setBeginWatchTime(System.currentTimeMillis());
        client.setLastHeartBeatTime(System.currentTimeMillis());
        client.setNickName(msg.getNickName());

        ClientManager.getInstance().addClient(client);
    }
}
