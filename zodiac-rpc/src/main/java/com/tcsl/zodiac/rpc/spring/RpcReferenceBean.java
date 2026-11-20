package com.tcsl.zodiac.rpc.spring;

import com.tcsl.zodiac.rpc.core.ConnectContext;
import com.tcsl.zodiac.rpc.core.RpcProxy;
import com.tcsl.zodiac.rpc.netty.ConnectCilent;
import com.tcsl.zodiac.rpc.serialize.Serializer;
import com.tcsl.zodiac.rpc.serialize.Serializers;
import com.tcsl.zodiac.rpc.spring.loadbalance.LoadBalance;
import com.tcsl.zodiac.rpc.spring.loadbalance.ServiceLoadBalancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;

/**
 * @className: RpcReferenceBean
 * @description:
 * @author: yang pan
 */
public class RpcReferenceBean implements FactoryBean<Object>, ApplicationContextAware, InitializingBean, DisposableBean {

    protected final Logger logger = LoggerFactory.getLogger(RpcReferenceBean.class);

    protected ApplicationContext applicationContext;

    private Class<?> classType;

    private RpcReference reference;

    private Serializer serializer = Serializers.getDefault();

    private Class<ConnectContext> connectMangenment =  ConnectContext.class;

    private String version = null;

    private long timeout ;

    private String serverUrl ;

    private String serverAddr ;

    private ServiceLoadBalancer loadBalancer ;

    public RpcReferenceBean(RpcReference reference, Class<?> classType) {
        this.reference = reference;
        this.classType = classType;
        this.timeout = reference.timeout();
        this.serverUrl = reference.serverUrl();
        LoadBalance loadBalance = reference.loadBalncer();
        loadBalancer = loadBalance.getServiceLoadBalancer();
    }

    @Override
    public void destroy() throws Exception {
        ConnectCilent connect = ConnectContext.getConnect(this.serverUrl);
        if (connect != null){
            connect.close();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object getObject() throws Exception {
        return RpcProxy.create(serverAddr, classType);
    }

    @Override
    public Class<?> getObjectType() {
        return this.classType;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("rpcRederenceBean afterPropertiesSet ");
        DiscoveryClient discoveryClient = this.applicationContext.getBean(DiscoveryClient.class);
        if (discoveryClient == null){
            throw  new RuntimeException("spring cloud discoveryClient not exist");
        }
        List<ServiceInstance> instances = discoveryClient.getInstances(this.serverUrl);
        ServiceInstance serviceInstance = loadBalancer.choose(this.serverUrl, instances);

        int port = serviceInstance.getPort()-1000;
        this.serverAddr = serviceInstance.getHost()+":"+port;
    }






}
