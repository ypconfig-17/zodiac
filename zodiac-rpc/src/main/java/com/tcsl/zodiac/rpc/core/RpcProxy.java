package com.tcsl.zodiac.rpc.core;

import com.tcsl.zodiac.comm.utils.IpUtils;
import com.tcsl.zodiac.rpc.exception.RpcException;
import com.tcsl.zodiac.rpc.netty.ConnectCilent;
import com.tcsl.zodiac.rpc.netty.FutureRpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @className: RpcProxy
 * @description:
 * @author: yang pan
 */
public class RpcProxy {

    private static final Logger logger = LoggerFactory.getLogger(RpcProxy.class);


    /**
     * 创建clint代理
     * @param url
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T create(String url, Class<T> clazz)  {

        if (!clazz.isInterface()){
            logger.error("rpc proxy class can only be specified on an interface ");
        }
        Object proxyInstance = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz}, (proxy, method, args) -> {
            String className = method.getDeclaringClass().getName();    // iface.getName()
            String methodName = method.getName();
            Class<?>[] parameterTypes = method.getParameterTypes();
            // filter method like "Object.toString()"
            if (className.equals(Object.class.getName())) {
                logger.info("rpc proxy class-method not support [{}#{}]", className, methodName);
                throw new RpcException("rpc proxy class-method not support");
            }

            String finalAddress = url;
            RpcRequest rpcRequest = new RpcRequest();
            rpcRequest.setRequestId(UUID.randomUUID().toString());
            rpcRequest.setInterfaceName(className);
            rpcRequest.setParameters(args);
            rpcRequest.setParameterTypes(parameterTypes);
            rpcRequest.setMethodName(methodName);
            FutureRpcResponse exchangeFuture = FutureRpcResponse.createExchangeFuture(rpcRequest);
            ConnectCilent connectCilent = ConnectContext.getConnect(finalAddress);
            connectCilent.send(rpcRequest);
            RpcResponse rpcResponse = exchangeFuture.get();
            return rpcResponse.getResult();
        });
        return (T) proxyInstance;
    }

    public static <T> T create(String host, int port , Class<T> clazz) {
       return create(IpUtils.getIpPort(host,port),clazz);
    }
}
