package com.tcsl.zodiac.cilent.register;

import com.tcsl.zodiac.cilent.cilent.ZodiacDiscoveryRegister;
import com.tcsl.zodiac.cilent.config.ZodiacDiscoverProperties;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.serviceregistry.Registration;

import java.net.URI;
import java.util.Map;

/**
 * @className: CapricornServiceRegister
 * @description:
 * @author: yang pan
 */
public class ZodiacRegistration implements Registration {

    private ZodiacDiscoveryRegister zodiacDiscoveryRegister;
    private ZodiacDiscoverProperties zodiacDiscoverProperties;

    public ZodiacRegistration(ZodiacDiscoverProperties zodiacDiscoverProperties, ZodiacDiscoveryRegister zodiacDiscoveryRegister) {
        this.zodiacDiscoverProperties = zodiacDiscoverProperties;
        this.zodiacDiscoveryRegister = zodiacDiscoveryRegister;

    }


    public ZodiacDiscoveryRegister getZodiacDiscoveryRegister() {
        return zodiacDiscoveryRegister;
    }

    @Override
    public String getServiceId() {
        return zodiacDiscoverProperties.getService();
    }

    @Override
    public String getHost() {
        return zodiacDiscoverProperties.getIp();
    }

    @Override
    public int getPort() {
        return zodiacDiscoverProperties.getPort();
    }

    public void setPort(int port) {
        this.zodiacDiscoverProperties.setPort(port);
    }

    @Override
    public boolean isSecure() {
        return zodiacDiscoverProperties.isSecure();
    }

    @Override
    public URI getUri() {
        return DefaultServiceInstance.getUri(this);
    }

    @Override
    public Map<String, String> getMetadata() {
        return zodiacDiscoverProperties.getMetadata();
    }

    public ZodiacDiscoverProperties getZodiacDiscoverProperties() {
        return zodiacDiscoverProperties;
    }

    public void setZodiacDiscoverProperties(ZodiacDiscoverProperties zodiacDiscoverProperties) {
        this.zodiacDiscoverProperties = zodiacDiscoverProperties;
    }


}
