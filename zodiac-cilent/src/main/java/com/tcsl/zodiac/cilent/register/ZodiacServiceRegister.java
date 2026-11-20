package com.tcsl.zodiac.cilent.register;

import com.tcsl.zodiac.cilent.cilent.InstanceStatus;
import com.tcsl.zodiac.cilent.cilent.ZodiacDiscoveryRegister;
import org.springframework.cloud.client.serviceregistry.ServiceRegistry;

/**
 * @className: CapricornServiceRegister
 * @description:
 * @author: yang pan
 */
public class ZodiacServiceRegister implements ServiceRegistry<ZodiacRegistration> {



    @Override
    public void register(ZodiacRegistration registration) {
        ZodiacDiscoveryRegister zodiacDiscoveryRegister = registration.getZodiacDiscoveryRegister();
        zodiacDiscoveryRegister .setPort(registration.getPort());
        zodiacDiscoveryRegister.register();
    }

    @Override
    public void deregister(ZodiacRegistration registration) {
        ZodiacDiscoveryRegister zodiacDiscoveryRegister = registration.getZodiacDiscoveryRegister();
        zodiacDiscoveryRegister .setPort(registration.getPort());
        zodiacDiscoveryRegister.deregister();
    }

    @Override
    public void close() {
    }

    @Override
    public void setStatus(ZodiacRegistration registration, String status) {

    }

    @Override
    public <T> T getStatus(ZodiacRegistration registration) {

        return null;
    }
}
