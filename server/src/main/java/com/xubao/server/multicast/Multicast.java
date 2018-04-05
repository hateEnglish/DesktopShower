package com.xubao.server.multicast;

import com.xubao.comment.config.CommentConfig;
import com.xubao.comment.contentStruct.Content;
import com.xubao.comment.contentStruct.ContentProvider;
import com.xubao.comment.util.NetAddress;
import com.xubao.server.base.ScreenShotManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @Author xubao
 * @Date 2018/2/22
 */
public class Multicast {
    private static Logger log = LoggerFactory.getLogger(Multicast.class);


    private Channel ch;
    private EventLoopGroup loopGroup;
    private InetSocketAddress groupAddress;
    private SocketAddress localAddress;

    private Bootstrap bootstrap;

    private int muiticastPort = CommentConfig.getInstance().getProperInt("server.default_multicast_port");
    private int multicastInterval = CommentConfig.getInstance().getProperInt("server.multicast_interval");
    private int multicastWaitInterval = CommentConfig.getInstance().getProperInt("server.multicast_wait_interval");

    //每次组播数据的最大长度
    private int perSendMaxSize = CommentConfig.getInstance().getProperInt("server.per_send_max_size");

    private Thread multicastThread;

    private MulticastStata multicastStata;

    private ContentProvider contentProvider;

    public enum MulticastStata {
        SEND,    //正在组播
        WAIT,    //等待组播
        STOP,    //停止组播
    }

    public Multicast(InetSocketAddress groupAddress,ContentProvider contentProvider) throws Exception {
        this.groupAddress = groupAddress;
        if (groupAddress.getPort() != 0) {
            muiticastPort = groupAddress.getPort();
        }
        localAddress = new InetSocketAddress(NetAddress.getLocalHostLANAddress(), groupAddress.getPort());

        multicastStata = MulticastStata.WAIT;
        this.contentProvider = contentProvider;
    }


    private void init() throws InterruptedException {

        loopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(loopGroup)
                .localAddress(localAddress)
                .channelFactory(new ChannelFactory<NioDatagramChannel>() {

                    public NioDatagramChannel newChannel() {
                        return new NioDatagramChannel(InternetProtocolFamily.IPv4);
                    }
                })
                .option(ChannelOption.IP_MULTICAST_IF, NetUtil.LOOPBACK_IF)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.RCVBUF_ALLOCATOR,new FixedRecvByteBufAllocator(65535))
                .handler(new ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    protected void initChannel(NioDatagramChannel ch) throws Exception {
                        //ch.pipeline().addLast(new LengthFieldBasedFrameDecoder())
                        ch.pipeline().addLast(new MuiticastHandler());
                    }
                });
                //.handler(new MuiticastHandler());
        ch = bootstrap.bind(0).await().channel();

//        ch.writeAndFlush(new DatagramPacket(
//                Unpooled.copiedBuffer("8888888888888888888", CharsetUtil.UTF_8),
//                groupAddress));
//        System.out.println("发送结束");
//        ch.close().awaitUninterruptibly();
    }

    public void initMulticastThread() {
        multicastThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (multicastStata == MulticastStata.WAIT) {
                        log.debug("等待发送组播消息");
                        try {
                            Thread.sleep(multicastWaitInterval);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            break;
                        }
                    } else if (multicastStata == MulticastStata.STOP) {
                        log.info("正在停止发送组播消息");
                        loopGroup.shutdownGracefully();
                        log.info("停止发送组播消息成功!");
                        break;
                    } else if (multicastStata == MulticastStata.SEND) {

                        //log.debug("正在发送组播消息");
                        byte[] data = null;
                        try {
                            Content content = contentProvider.getContent();
                            if(content==null){
                                log.debug("内容为空不发送");
                                continue;
                            }
                            data = content.getData();
                        } catch (Exception e) {
                            log.info("获取内容数据时报错");
                            e.printStackTrace();
                        }

                        if(data==null){
                            log.debug("数据为空不发送!!");
                            //睡一段时间
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            continue;
                        }
                        //log.debug("数据总长度:"+data.length);
//                        int perSendMaxSize = 2048;

//                        ByteBuf byteBuf = Unpooled.wrappedBuffer(data);
//                        for(int i=0;i<data.length;i+=perSendMaxSize){
//                            int length = i<data.length?data.length%perSendMaxSize:perSendMaxSize;
//                            multicast(multicastMsgBuild(byteBuf.retainedSlice(i,length),groupAddress));
//                            try {
//                                Thread.sleep(multicastInterval/(data.length/perSendMaxSize));
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                                break;
//                            }
//                        }
	                    doMulticast(data,contentProvider.getNextContentNumber());

                        //multicast(multicastMsgBuild(data,groupAddress));
//                        try {
//                            Thread.sleep(multicastInterval);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                            break;
//                        }
                    }
                }
            }
        });
    }

    private void doMulticast(byte[] data,int dataNumber){
        //log.debug("数据总长度:"+data.length);


        ByteBuf byteBuf = Unpooled.wrappedBuffer(data);
        int pieceNumber = 0;
        for(int i=0;i<data.length;i+=perSendMaxSize){
            int length = i+perSendMaxSize<data.length?perSendMaxSize:data.length%perSendMaxSize;
            pieceNumber++;

            ByteBuf buf = Unpooled.buffer(perSendMaxSize);
            addMulticastHeader(buf,data.length,length,dataNumber,pieceNumber);
            buf.writeBytes(byteBuf,length);

            multicast(multicastMsgBuild(buf,groupAddress));
            try {
                Thread.sleep(multicastInterval/(data.length/perSendMaxSize));
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void addMulticastHeader(ByteBuf buf,long dataLength,int pieceLength,int dataNumber,int pieceNumber) {
        buf.writeLong(dataLength);
        buf.writeInt(pieceLength);
        buf.writeInt(dataNumber);
        buf.writeInt(pieceNumber);
    }

    public void multicastStart() throws InterruptedException {
        init();
        initMulticastThread();
        multicastStata = MulticastStata.SEND;

        multicastThread.start();
    }

    public void multicastStop(){
        multicastStata = MulticastStata.STOP;
    }

    public void multicastWait(){
        multicastStata = MulticastStata.WAIT;
    }

    private void multicast(Object msg){
        ch.writeAndFlush(msg);
    }

    private DatagramPacket multicastMsgBuild(ByteBuf data, InetSocketAddress groupAddress) {
        DatagramPacket datagramPacket = new DatagramPacket(data, groupAddress);
        return datagramPacket;
    }

    private DatagramPacket multicastMsgBuild(byte[] data,InetSocketAddress groupAddress){
        return multicastMsgBuild(Unpooled.wrappedBuffer(data),groupAddress);
    }

    private static class MuiticastHandler extends SimpleChannelInboundHandler<DatagramPacket> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
            log.debug("收到组播消息");
        }
    }

    public static void main(String[] args) throws Exception {
        String multicastHost = CommentConfig.getInstance().getProper("server.multicast_address");
        int multicastPort = CommentConfig.getInstance().getProperInt("server.default_multicast_port");

        InetSocketAddress groupAddress = new InetSocketAddress(multicastHost, multicastPort);
        Rectangle shotArea = new Rectangle(800,800);
        ScreenShotManager screenShotManager = new ScreenShotManager(30,50,shotArea);
        screenShotManager.beginShot();

        Multicast multicast = new Multicast(groupAddress,screenShotManager);
       // multicast.init();
        multicast.multicastStart();
    }
}
