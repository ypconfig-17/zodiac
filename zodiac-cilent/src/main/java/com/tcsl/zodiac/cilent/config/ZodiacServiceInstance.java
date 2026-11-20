package com.tcsl.zodiac.cilent.config;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;

import java.net.URI;
import java.util.Map;

/**
 * @className: ZodiacServiceInstance
 * @description:
 * @author: yang pan
 */
public class ZodiacServiceInstance implements ServiceInstance {

    private String serviceId;
    private String host;
    private int port;
    private boolean secure;
    private Map<String, String> metadata;

    @Override
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public URI getUri() {
        return DefaultServiceInstance.getUri(this);
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    @Override
    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
