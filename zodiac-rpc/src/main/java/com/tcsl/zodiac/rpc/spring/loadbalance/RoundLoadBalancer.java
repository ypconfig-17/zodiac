package com.tcsl.zodiac.rpc.spring.loadbalance;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.cloud.client.ServiceInstance;

import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * @className: RoundLoadBalancer
 * @description:
 * @author: yang pan
 */
public class RoundLoadBalancer implements ServiceLoadBalancer {

    Cache<String, Integer> roundMap = CacheBuilder.newBuilder()
            .maximumSize(100)//最多存放
            .concurrencyLevel(12)
            .expireAfterWrite(1, TimeUnit.DAYS)
            .recordStats()
            .build();

    @Override
    public ServiceInstance choose(String serviceId, List<ServiceInstance> instanceList) {

        Integer count = null;

        try {
             count = roundMap.get(serviceId, () -> null);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (count == null){
             count = new Random().nextInt(100);
             count++;
        }
        roundMap.put(serviceId,count);
        return instanceList.get(count%instanceList.size());


    }
}
