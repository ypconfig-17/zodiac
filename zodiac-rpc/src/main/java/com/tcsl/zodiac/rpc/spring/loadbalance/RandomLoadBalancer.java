package com.tcsl.zodiac.rpc.spring.loadbalance;

import org.springframework.cloud.client.ServiceInstance;

import java.util.List;
import java.util.Random;

/**
 * @className: RoundLoadBalancer
 * @description:
 * @author: yang pan
 */
public class RandomLoadBalancer implements ServiceLoadBalancer {

    @Override
    public ServiceInstance choose(String serviceId, List<ServiceInstance> instanceList) {
        if (instanceList.size() == 1){
            return instanceList.get(0);
        }
        int index = new Random(instanceList.size() - 1).nextInt();
        return instanceList.get(index);
    }
}
