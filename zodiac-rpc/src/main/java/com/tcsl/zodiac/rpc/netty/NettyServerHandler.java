package com.tcsl.zodiac.rpc.netty;

import com.tcsl.zodiac.rpc.core.ServiceContext;
import com.tcsl.zodiac.rpc.core.RpcRequest;
import com.tcsl.zodiac.rpc.core.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @className: NettyServerHandler
 * @description:
 * @author: yang pan
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
        logger.info("rpcRequest paramters:{}",rpcRequest);

        if (Beat.BEAT_ID.equalsIgnoreCase(rpcRequest.getRequestId())){
            logger.debug("rpc provider netty server read beat-ping:{}",rpcRequest);
            return;
        }
        String serviceName = rpcRequest.getInterfaceName();
        String methodName = rpcRequest.getMethodName();
        Object[] parameters = rpcRequest.getParameters();
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();

        Object serviceBean = ServiceContext.getService(serviceName);
        // validate

        Object entity = null;
        Exception exception = null;
        try {
            validServieBean(serviceBean);
            entity = MethodUtils.invokeMethod(serviceBean, methodName, parameters, parameterTypes);
        } catch (Exception e) {
            exception = e;
        }

        logger.info("Read {} and invoke the {}'s method[name:{}, param-types:{}, params:{}] : {}",
                rpcRequest, serviceName, methodName, parameterTypes, parameters, entity);

        RpcResponse response = new RpcResponse();
        response.setRequestId(rpcRequest.getRequestId());
        response.setResult(entity);
        response.setException(exception);

        ctx.writeAndFlush(response);
        logger.info("write and flush response{}", response);
    }

    private void  validServieBean(Object serviceBean) throws  Exception{

        // valid
        if (serviceBean == null) {
            throw  new RuntimeException("service provider not exist");
        }

    }
}
