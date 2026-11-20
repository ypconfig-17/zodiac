package com.tcsl.zodiac.cilent.discovery;

import com.tcsl.zodiac.cilent.cilent.InstanceInfo;
import com.tcsl.zodiac.cilent.cilent.ZodiacDiscoveryRegister;
import com.tcsl.zodiac.cilent.config.ZodiacServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.*;

/**
 * @className: CapricornServiceRegister
 * @description:
 * @author: yang pan
 */
public class ZodiacDiscoveryCilent implements DiscoveryClient {

    private ZodiacDiscoveryRegister zodiacDiscoveryRegister;

    public static final String DESCRIPTION = "Spring Cloud Zodiac Discovery Client";

    public ZodiacDiscoveryCilent(ZodiacDiscoveryRegister zodiacDiscoveryRegister) {
        this.zodiacDiscoveryRegister=zodiacDiscoveryRegister;
    }

    @Override
    public String description() {
        return DESCRIPTION;
    }

    @Override
    public List<ServiceInstance> getInstances(String serviceId) {
        List<InstanceInfo> instances = zodiacDiscoveryRegister.getInstances(serviceId);
       return hostToServiceInstanceList(instances,serviceId);
    }

    public static List<ServiceInstance> hostToServiceInstanceList( List<InstanceInfo> instances, String serviceId) {
        List<ServiceInstance> result = new ArrayList<>();
        for (InstanceInfo instanceInfo : instances ){
            ServiceInstance serviceInstance = hostToServiceInstance(instanceInfo, serviceId);
            if (serviceInstance != null) {
                result.add(serviceInstance);
            }
        }

        return result;
    }
    public static ServiceInstance hostToServiceInstance(InstanceInfo instance,  String serviceId) {
//        if (instance == null || !instance.isEnabled() || !instance.isHealthy()) {
//            return null;
//        }
        ZodiacServiceInstance zodiacServiceInstance = new ZodiacServiceInstance();
        zodiacServiceInstance.setHost(instance.getIp());
        zodiacServiceInstance.setPort(instance.getPort());
        zodiacServiceInstance.setServiceId(serviceId);
        return zodiacServiceInstance;
    }
    @Override
    public List<String> getServices() {
        return zodiacDiscoveryRegister.getServices();
    }
}
