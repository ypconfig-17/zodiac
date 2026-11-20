package com.tcsl.zodiac.rpc.core;

import com.tcsl.zodiac.rpc.netty.ConnectCilent;
import com.tcsl.zodiac.rpc.netty.NettyConnectClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @className: ConnectMangenment
 * @description:
 * @author: yang pan
 */
public class ConnectContext {

    private final static Logger logger = LoggerFactory.getLogger(ConnectContext.class);

    private static Map<String, ConnectCilent>  connectPool = new ConcurrentHashMap<>();


    public static ConnectCilent getConnect(String address){
        ConnectCilent connectCilent = connectPool.get(address);
        if(connectCilent !=null){
            return connectCilent;
        }
        connectCilent = new NettyConnectClient(address);
        connectPool.putIfAbsent(address,connectCilent);
        return connectCilent;
    }



    public static void shutDown(){
        for (ConnectCilent connectCilent:  connectPool.values()){
            try {
                connectCilent.close();
            } catch (Exception e) {
                logger.error("connectcontext shubDown error ",e);
            }

        }
    }












}
