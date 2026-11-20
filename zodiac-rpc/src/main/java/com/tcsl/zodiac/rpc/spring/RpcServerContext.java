package com.tcsl.zodiac.rpc.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @className: RpcServerMangement
 * @description:
 * @author: yang pan
 */
public class RpcServerContext implements ApplicationContextAware, InitializingBean, DisposableBean {

    public static void main(String[] args) {

    }

    protected final Logger logger = LoggerFactory.getLogger(RpcReferenceBean.class);

    private ApplicationContext applicationContext;

    private static final Map<String,Object>  reposity = new ConcurrentHashMap<>();

    private int prot = 25500;
    private String serviceName;

    private String interfaceName;

    private Class<?> classType;





    @Override
    public void destroy() throws Exception {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

}
