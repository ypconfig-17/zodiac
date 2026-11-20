package com.tcsl.zodiac.rpc.spring.loadbalance;


/**
 * @className: LoadBalance
 * @description:
 * @author: yang pan
 */
public enum LoadBalance {

    ROUND(new RoundLoadBalancer()),

    RANDOM(new RandomLoadBalancer());

    private ServiceLoadBalancer serviceLoadBalancer;

    LoadBalance(ServiceLoadBalancer serviceLoadBalancer) {
        this.serviceLoadBalancer = serviceLoadBalancer;
    }

    public ServiceLoadBalancer getServiceLoadBalancer() {
        return serviceLoadBalancer;
    }
}
