package com.tcsl.zodiac.rpc.core;

import com.sun.xml.internal.messaging.saaj.soap.impl.EnvelopeImpl;
import com.tcsl.zodiac.rpc.netty.NettyConnectServer;
import com.tcsl.zodiac.rpc.serialize.Serializers;
import com.tcsl.zodiac.rpc.spring.RpcReferenceBean;
import com.tcsl.zodiac.rpc.spring.RpcServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @className: ServiceContext
 * @description: services 上下文容器
 * @author: yang pan
 */
@Component
public class ServiceContext implements InitializingBean ,EnvironmentAware{

    private final static Logger logger = LoggerFactory.getLogger(RpcReferenceBean.class);

    private static final Map<String, Object> services = new ConcurrentHashMap<>();
    private static final Set<String> servicesNames = new ConcurrentSkipListSet<>();
    @Autowired
    private RpcServerProperties rpcServerProperties;

    public static void loadService(String serviceName, Object service) {
        services.putIfAbsent(serviceName, service);
    }

    public static void loadServiceName(String serviceName) {
        servicesNames.add(serviceName);
    }


    public static Object getService(String serviceName) {
        if (serviceName == null){
            return null;
        }
        return services.get(serviceName);
    }

    public static Set<String> getServiceNames() {
        return Collections.unmodifiableSet(servicesNames);
    }




    @Override
    public void afterPropertiesSet() throws Exception {

        Integer serverPort = environment.getProperty("server.port",Integer.class);

        int port = serverPort-1000;
        Thread thread = new Thread(() -> {
            NettyConnectServer nettyConnectServer = new NettyConnectServer(Serializers.getDefault(),port );
            nettyConnectServer.accept();
        });
        thread.start();
    }

   private Environment environment;
    @Override
    public void setEnvironment(Environment environment) {
        this.environment=environment;
    }
}
