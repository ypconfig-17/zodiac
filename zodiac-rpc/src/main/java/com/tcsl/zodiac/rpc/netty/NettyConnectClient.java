package com.tcsl.zodiac.rpc.netty;

import com.tcsl.zodiac.comm.utils.IpUtils;
import com.tcsl.zodiac.rpc.core.RpcRequest;
import com.tcsl.zodiac.rpc.core.RpcResponse;
import com.tcsl.zodiac.rpc.netty.codec.NettyDecoder;
import com.tcsl.zodiac.rpc.netty.codec.NettyEncoder;
import com.tcsl.zodiac.rpc.serialize.Serializer;
import com.tcsl.zodiac.rpc.serialize.Serializers;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @className: NettyConnectCilent
 * @description:
 * @author: yang pan
 */
public class NettyConnectClient implements ConnectCilent {

    private static Logger logger = LoggerFactory.getLogger(NettyConnectClient.class);

    private final NioEventLoopGroup nioEventLoopGroup;
    private final Bootstrap bootstrap;
    private Channel channel;
    private Serializer serializer = Serializers.getDefault();
    private String host;
    private int port;
    private Object lock = new Object();

    @Override
    public void close() {
        synchronized (this.lock){
            if (channel !=null && channel.isActive()){
                channel.close();
            }
            if (nioEventLoopGroup != null) {
                nioEventLoopGroup.shutdownGracefully();
            }


        }

    }

    @Override
    public void send(RpcRequest rpcRequest) throws Exception {
        this.channel.writeAndFlush(rpcRequest).sync();
    }


    public NettyConnectClient(String address) {
        // group
        this.nioEventLoopGroup = new NioEventLoopGroup();


        // address
        Object[] addr = IpUtils.parseIpPort(address);
        this.host = (String) addr[0];
        this.port = (int) addr[1];
        final NettyConnectClient thisClient = this;
        this.bootstrap = new Bootstrap();
        bootstrap.group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new IdleStateHandler(0, 0, Beat.BEAT_INTERVAL, TimeUnit.SECONDS))    // beat N, close if fail
                                .addLast(new NettyEncoder(RpcRequest.class, serializer))
                                .addLast(new NettyDecoder(RpcResponse.class, serializer))
                                .addLast(new NettyClientHandler(thisClient));
                    }
                })
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);

        try {
            this.channel = bootstrap.connect(host, port).sync().channel();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
            logger.error("init netty cilent fail", interruptedException);
            throw new RuntimeException("init netty cilent fail");
        }

        // valid
        if (!isValidate()) {
            return;
        }

        logger.debug("rpc netty cilent  connect to server success at host:{}, port:{}", host, port);

    }


    public boolean isValidate() {
        if (this.channel != null) {
            return this.channel.isActive();
        }
        return false;
    }


}
