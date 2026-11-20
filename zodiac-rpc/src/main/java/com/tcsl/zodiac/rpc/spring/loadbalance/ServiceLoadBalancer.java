package com.tcsl.zodiac.rpc.spring.loadbalance;

import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

/**
 * @className: ServiceInstanceChooser
 * @description:
 * @author: yang pan
 */
public interface ServiceLoadBalancer {

     ServiceInstance choose(String serviceId, List<ServiceInstance> instanceList);
}
