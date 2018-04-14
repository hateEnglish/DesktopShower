package com.xubao.client.multicastReceive;

import com.xubao.client.manager.FrameManager;
import com.xubao.client.pojo.ReceiveFrame;
import com.xubao.client.pojo.ReceiveFramePiece;
import com.xubao.comment.config.CommentConfig;
import com.xubao.comment.util.NetAddress;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.TimeUnit;

/**
 * @author xubao
 * @version 1.0
 * @since 2018/2/28
 */
public class MulticastReceive {
    private static Logger log = LoggerFactory.getLogger(MulticastReceive.class);

    private EventLoopGroup loopGroup;
    private InetSocketAddress localAddress;
    private InetSocketAddress groupAddress;
    private int muiticastPort = CommentConfig.getInstance().getProperInt("server.default_multicast_port");

    public MulticastReceive(InetSocketAddress groupAddress) throws Exception {
        this.groupAddress = groupAddress;
        if (groupAddress.getPort() != 0) {
            muiticastPort = groupAddress.getPort();
        }
        localAddress = new InetSocketAddress(NetAddress.getLocalHostLANAddress(), muiticastPort);
    }

    public void init() throws InterruptedException {
        loopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(loopGroup)
                .channelFactory(new ChannelFactory<NioDatagramChannel>() {

                    public NioDatagramChannel newChannel() {
                        return new NioDatagramChannel(InternetProtocolFamily.IPv4);
                    }
                })
                .localAddress(localAddress)
                //.option(ChannelOption.IP_MULTICAST_IF,NetUtil.LOOPBACK_IF)
                //.option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.IP_MULTICAST_LOOP_DISABLED, true)
                .option(ChannelOption.SO_RCVBUF, 65535)
                //.option(ChannelOption.IP_MULTICAST_TTL, 255)
                .option(ChannelOption.SO_REUSEADDR, true)
                .handler(new ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    public void initChannel(NioDatagramChannel ch) throws Exception {
                        ch.pipeline().addLast(new MulticastReceiveHandler());
                    }
                });

        NioDatagramChannel ch = (NioDatagramChannel) bootstrap.bind(groupAddress.getPort()).sync().channel();
        ch.joinGroup(groupAddress, getIF(true)).sync();
        System.out.println("server");
        //ch.closeFuture().sync().awaitUninterruptibly();
    }

    private NetworkInterface getIF(boolean loopback){
        if(loopback){
            return NetUtil.LOOPBACK_IF;
        }else{
            try {
                NetworkInterface IF = NetworkInterface.getByInetAddress(InetAddress.getByName(localAddress.getHostName()));
                System.out.println("IF="+IF);
                return IF;
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void stopReceive() {
        loopGroup.shutdownGracefully();
    }

    private static class MulticastReceiveHandler extends SimpleChannelInboundHandler<DatagramPacket> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {

            long dataSize = msg.content().readLong();
            int dataPieceSize = msg.content().readInt();
            int frameNumber = msg.content().readInt();
            int pieceNumber = msg.content().readInt();
            //log.debug("收到组播消息 帧号:" + frameNumber + " 碎片号:" + pieceNumber + " 数据大小:" + dataSize + " 当前传输:" + dataPieceSize);

            byte[] buf = new byte[msg.content().readableBytes()];
            msg.content().readBytes(buf);

            ReceiveFramePiece receiveFramePiece = new ReceiveFramePiece(frameNumber, pieceNumber, dataSize, dataPieceSize, buf);
            FrameManager.getInstance().addFramePiece(receiveFramePiece);

//			while(msg.content().readableBytes()!=0){
//				msg.content().readBytes(buf);
//				baos.write(buf);
//			}
//			receiveFrame.setData(baos.toByteArray());
//			FrameManager.getInstance().addFrame(receiveFrame);
//			log.debug("添加帧数据成功 dataSize="+receiveFrame.getData().length);
//


        }
    }


    public static void main(String[] args) throws Exception {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("开始保存帧");

                while (true) {
                    try {
                        ReceiveFrame rf = FrameManager.getInstance().getAndWaitFirstFrameFull(10, TimeUnit.SECONDS, false, true);
                        FileOutputStream fos = new FileOutputStream("z://" + rf.getFrameNumber() + ".jpg");
                        rf.writeData(fos);
                        fos.close();
                        System.out.println("保存结束" + "z://" + rf.getFrameNumber() + ".jpg 结束");

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        String multicastHost = CommentConfig.getInstance().getProper("server.multicast_address");
        int multicastPort = CommentConfig.getInstance().getProperInt("server.default_multicast_port");

        InetSocketAddress groupAddress = new InetSocketAddress(multicastHost, multicastPort);
        MulticastReceive multicast = new MulticastReceive(groupAddress);
        multicast.init();


    }
}
