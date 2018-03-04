package com.xubao.client.connection.processor;

import com.xubao.client.manager.ServerManager;
import com.xubao.client.pojo.ServerInfo;
import com.xubao.comment.proto.Connection;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author xubao
 * @Date 2018/2/20
 */
public class BroadcastProcessor implements Processor<Connection.Broadcast>{
    private static Logger log = LoggerFactory.getLogger(BroadcastProcessor.class);

    @Override
    public void process(ChannelHandlerContext ctx, Connection.Broadcast msg) {
        log.debug("有广播消息msg={}",msg);
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setAddress(ctx.channel().remoteAddress());
        serverInfo.setConnPort(msg.getServerConnPort());
        serverInfo.setNickName(msg.getServerNickName());
        serverInfo.setCommend(msg.getComment());
        serverInfo.setLastReceiveTime(System.currentTimeMillis());

        ServerManager.getInstance().addServerInfo(serverInfo);
    }
}
