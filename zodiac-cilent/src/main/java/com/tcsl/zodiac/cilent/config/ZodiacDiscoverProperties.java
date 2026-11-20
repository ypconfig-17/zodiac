package com.tcsl.zodiac.cilent.config;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.net.SocketException;
import java.util.*;

/**
 * @className: ZodiacDiscoverProperties
 * @description:
 * @author: yang pan
 */
@ConfigurationProperties(prefix = "spring.cloud.zodiac.discover")
@Configuration
@Data
public class ZodiacDiscoverProperties {
    private static final Logger log = LoggerFactory.getLogger(ZodiacDiscoverProperties.class);
    private String serverAddr = "127.0.0.1:8041/zodiac";
    private String namespace;
    private long watchDelay = 30000L;
    @Value("${spring.cloud.zodiac.discovery.service:${spring.application.name:}}")
    private String service;
    private float weight = 1.0F;
    private String clusterName = "DEFAULT";
    private Map<String, String> metadata = new HashMap();
    private String ip;
    private int port = -1;
    private boolean secure = false;

    @Autowired
    private InetUtils inetUtils;
    @Autowired
    private Environment environment;

    private Properties properties  = new Properties();

    @NestedConfigurationProperty
    private ZodiacCilentProperties cilent = new ZodiacCilentProperties();

    public Properties getProperties() {
        return properties;
    }


    @PostConstruct
    public void init() throws SocketException {
        log.info("properties init start ");
        this.metadata.put("preserved.register.source", "SPRING_CLOUD");
        if (this.secure) {
            this.metadata.put("secure", "true");
        }

        this.serverAddr = Objects.toString(this.serverAddr, "");
//        if (this.serverAddr.lastIndexOf("/") != -1) {
//            this.serverAddr = this.serverAddr.substring(0, this.serverAddr.length() - 1);
//        }

        this.namespace = Objects.toString(this.namespace, "");
        if (!StringUtils.hasText(this.ip)) {
            this.ip = this.inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
//            this.properties.setProperty("spring.cloud.zodiac.discover.hostName",ip);
        }

        this.initProperties(this.environment);
    }

    private void initProperties(Environment environment) {

        this.properties.setProperty("serverAddr",this.serverAddr);
        this.properties.setProperty("hostName",this.ip);
        this.properties.setProperty("namespace",this.namespace);
        this.properties.setProperty("service",this.service);
    }

}
