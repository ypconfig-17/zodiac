package com.tcsl.zodiac.rpc.netty;


import com.tcsl.zodiac.rpc.core.RpcRequest;
import com.tcsl.zodiac.rpc.core.RpcResponse;
import com.tcsl.zodiac.rpc.netty.codec.NettyDecoder;
import com.tcsl.zodiac.rpc.netty.codec.NettyEncoder;
import com.tcsl.zodiac.rpc.serialize.Serializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @className: NettyServer
 * @description:
 * @author: yang pan
 */
public class NettyConnectServer implements ConnectServer{

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    private Serializer serializer ;
    private EventLoopGroup bossGroup ;
    private EventLoopGroup workerGroup ;
    private int port;
    private Object lock = new Object();

    public NettyConnectServer(Serializer serializer, int port) {
        this.workerGroup = new NioEventLoopGroup();
        this.bossGroup = new NioEventLoopGroup();
        this.serializer =  serializer;
        this.port = port;
    }
    @Override
    public void accept() {
        synchronized (this.lock){
            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel channel) throws Exception {
                                channel.pipeline()
                                        .addLast(new IdleStateHandler(0,0, Beat.BEAT_INTERVAL*3, TimeUnit.SECONDS))     // beat 3N, close if idle
                                        .addLast(new NettyDecoder(RpcRequest.class, serializer))
                                        .addLast(new NettyEncoder(RpcResponse.class, serializer))
                                        .addLast(new NettyServerHandler());
                            }
                        })
                        .childOption(ChannelOption.TCP_NODELAY, true)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);

                // bind
                ChannelFuture future = bootstrap.bind(port).sync();
                logger.info("rpc remoting server start success, nettype:{}, port:{}", NettyConnectServer.class.getName(),port);
                // wait util stop
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                if (e instanceof InterruptedException) {
                    logger.info("rpc remoting server stop.");
                } else {
                    logger.error("rpc remoting server error.", e);
                }
            }
            System.out.println(11);


        }

    }

    @Override
    public void close() {
        synchronized (lock){
            try {
                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

        }

    }
}
