package com.xubao.server.broadcast;

import com.google.protobuf.Message;
import com.xubao.comment.config.CommentConfig;
import com.xubao.comment.message.MsgEncoding;
import com.xubao.comment.proto.Connection;
import com.xubao.comment.util.NetAddress;
import com.xubao.server.manager.ServerInfoManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.DatagramPacketDecoder;
import io.netty.handler.codec.DatagramPacketEncoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * @Author xubao
 * @Date 2018/2/18
 */
public class Broadcast {
    private static Logger log = LoggerFactory.getLogger(Broadcast.class);

    private int broadcastPort = CommentConfig.getInstance().getProperInt("server.broadcast_port");
    private int broadcastInterval = CommentConfig.getInstance().getProperInt("server.broadcast_interval");

    private String multicastHost = CommentConfig.getInstance().getProper("server.multicast_address");
    private int multicastPort = CommentConfig.getInstance().getProperInt("server.default_multicast_port");

    private int connPort = CommentConfig.getInstance().getProperInt("server.conn_port");

    private InetSocketAddress bradcastAddress;
    private Message msg;

    private Channel ch;
    private ClientHandler cLientHandler = new ClientHandler();
    private DatagramPacketDecoder dpd = new DatagramPacketDecoder(new ProtobufDecoder(Connection.BaseMsg.getDefaultInstance()));
    private DatagramPacketEncoder dpe = new DatagramPacketEncoder<Connection.BaseMsg>(new ProtobufEncoder());

    private EventLoopGroup group;
    private InetAddress localAddress;
    private InetAddress bradcast;


    public void init() throws Exception {

        localAddress= NetAddress.getLocalHostLANAddress();
        if(localAddress.isLoopbackAddress()){
            new Exception("未在局域网中");
        }
        bradcast = NetAddress.getLocalBroadCast();

        bradcastAddress = new InetSocketAddress(bradcast, broadcastPort);

        group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChannelInitializer<DatagramChannel>() {
                        @Override
                        protected void initChannel(DatagramChannel ch) throws Exception {
                            //ch.pipeline().addLast(dpd);
                            // ch.pipeline().addLast(dpe);
                            ch.pipeline().addLast(cLientHandler);
                        }
                    });
            ch = b.bind(0).await().channel();

//            ch.writeAndFlush(new DatagramPacket(
//                    Unpooled.copiedBuffer("Searh:", CharsetUtil.UTF_8),
//                    new InetSocketAddress("192.168.0.255", broadcastPort))).sync();

            // QuoteOfTheMomentClientHandler will close the DatagramChannel when a
            // response is received.  If the channel is not closed within 5 seconds,
            // print an error message and quit.
//            if (!ch.closeFuture().await(5000)) {
//                System.err.println("Search request timed out.");
//            }
        } catch (Exception e) {
            e.printStackTrace();
            group.shutdownGracefully();
        }
//        finally {
//            group.shutdownGracefully();
//        }
    }

    private void defaultMsg(){
        Connection.Broadcast.Builder builder = Connection.Broadcast.newBuilder();
        builder.setServerNickName("nickName");
        builder.setDriver("windows");
        builder.setMulticastAddress(multicastHost+":"+multicastPort);
        builder.setConnAddress(localAddress.getHostAddress()+":"+connPort);
        builder.setComment(ServerInfoManager.getInstance().showTheme);
        builder.setIsNeedPwd(ServerInfoManager.getInstance().isNeedPwd);
        builder.setWatchPwd(ServerInfoManager.getInstance().watchPwd);

        msg = builder.build();
    }

    public Message getMsg() {
        return msg;
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }

    private Thread broadcastThread;

    public void broadcastMsg(Message msg){
        Connection.BaseMsg baseMsg = MsgEncoding.encode(msg);
        DatagramPacket packet = new DatagramPacket(Unpooled.copiedBuffer(baseMsg.toByteArray()), bradcastAddress);
        ch.writeAndFlush(packet);
    }

    public void initBroadcastThread() {
        if(broadcastThread!=null){
            broadcastThread.interrupt();
        }
        broadcastThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    init();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                defaultMsg();
                while (true) {
                    try {
                        Thread.sleep(broadcastInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                    broadcastMsg(msg);
                }
            }
        });

        broadcastThread.setDaemon(true);
    }

    public void startBroadcast(){
        broadcastThread.start();
    }

    public void stopBroadcastThread() {
        broadcastThread.interrupt();
        group.shutdownGracefully();
    }

    public static void main(String[] args) {
//        Broadcast broadcast = new Broadcast();
//        broadcast.init();
//        try {
//            InetSocketAddress address = new InetSocketAddress("192.168.0.255", broadcast.broadcastPort);
////            broadcast.ch.writeAndFlush(new DatagramPacket(
////                    Unpooled.copiedBuffer("Searh:", CharsetUtil.UTF_8),
////                    new InetSocketAddress("192.168.0.255", broadcast.broadcastPort))).sync();
//            Connection.Broadcast build = Connection.Broadcast.newBuilder().setServerConnPort(1100).setServerNickName("hahah").build();
//            //DefaultAddressedEnvelope<Connection.BaseMsg, InetSocketAddress> packet = new DefaultAddressedEnvelope(MsgEncoding.encode(build),new InetSocketAddress("192.168.0.255", broadcast.broadcastPort));
//            Connection.BaseMsg msg = MsgEncoding.encode(build);
//            System.out.println("消息字节数" + msg.toByteArray().length);
//            DatagramPacket packet = new DatagramPacket(Unpooled.copiedBuffer(msg.toByteArray()), address);
//            broadcast.ch.writeAndFlush(packet);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        Broadcast broadcast = new Broadcast();
        broadcast.initBroadcastThread();
        broadcast.broadcastThread.start();

        try {
            Thread.sleep(1000*60*5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx,
                                    DatagramPacket packet) throws Exception {

        }
    }
}
