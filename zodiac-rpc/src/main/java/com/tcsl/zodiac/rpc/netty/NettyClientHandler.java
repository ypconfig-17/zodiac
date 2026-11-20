package com.tcsl.zodiac.rpc.netty;

import com.tcsl.zodiac.rpc.core.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @className: NettyClientHandler
 * @description:
 * @author: yang pan
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private static Logger logger = LoggerFactory.getLogger(NettyConnectClient.class);

    private NettyConnectClient nettyConnectClient;

    public NettyClientHandler(NettyConnectClient nettyConnectClient) {
        this.nettyConnectClient = nettyConnectClient;

    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        String requestId = rpcResponse.getRequestId();
        FutureRpcResponse futureRpcResponse = FutureRpcResponse.removeExchangeFuture(requestId);
        if (futureRpcResponse != null){
            Promise promise = futureRpcResponse.getPromise();
            promise.setSuccess(rpcResponse);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("rpc netty 客户端捕获异常", cause);
        ctx.close();
    }
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            // beat N, close if fail(may throw error)
            nettyConnectClient.send(Beat.BEAT_PING);
            logger.debug("rpc netty 客户端发送beat-ping");
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
