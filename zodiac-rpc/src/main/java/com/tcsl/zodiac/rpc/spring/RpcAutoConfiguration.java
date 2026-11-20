package com.tcsl.zodiac.rpc.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @className: RpcAutoConfiguration
 * @description:
 * @author: yang pan
 */
@Configuration
public class RpcAutoConfiguration implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }




    @Bean
    @ConditionalOnMissingBean(RpcInjectBeanPostProcessor.class)
    public RpcInjectBeanPostProcessor rpcInjectBeanPostProcessor() {
        return new RpcInjectBeanPostProcessor();
    }


    @Bean
    @ConditionalOnMissingBean(RpcServiceBeanPostProcessor.class)
    public RpcServiceBeanPostProcessor rpcServiceBeanPostProcessor() {
        return new RpcServiceBeanPostProcessor();
    }
}
