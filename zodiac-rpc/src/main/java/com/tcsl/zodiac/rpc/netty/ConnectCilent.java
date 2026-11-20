package com.tcsl.zodiac.rpc.netty;

import com.tcsl.zodiac.rpc.core.RpcRequest;

/**
 * @className: ConnectCilent
 * @description:
 * @author: yang pan
 */
public interface ConnectCilent  extends AutoCloseable{


    void send(RpcRequest rpcRequest)throws Exception;
}
