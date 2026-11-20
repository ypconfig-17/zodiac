package com.tcsl.zodiac.cilent.config;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @className: ZodiacDiscoverProperties
 * @description:
 * @author: yang pan
 */
@Data
public class ZodiacCilentProperties {
    private static final Logger log = LoggerFactory.getLogger(ZodiacCilentProperties.class);

    public static final String DEFAULT_URL = "http://localhost:9168/" ;

    private static final int MINUTES = 60;
    /**
     * 用于指示 客户端已启用的标志
     */
    private boolean enabled = true;

    /**
     * 指示从 服务器获取注册表信息的频率（以秒为单位）。
     */
    private int registryFetchIntervalSeconds = 30;

    /**
     * 指示复制要复制到 服务器的实例更改的频率（以秒为单位）。
     */
    private int instanceInfoReplicationIntervalSeconds = 30;

    /**
     * 指示最初将实例信息复制到 服务器的时间（以秒为单位）。
     */
    private int initialInstanceInfoReplicationIntervalSeconds = 40;

    /**
     * 指示轮询尤里卡服务器信息更改的频率（以秒为单位）。可以添加或删除 服务器，此设置控制 客户端应该多久知道它。
     */
    private int eurekaServiceUrlPollIntervalSeconds = 5 * MINUTES;


    /**
     *获取实现 BackupRegistry 以仅在  客户端启动时第一次获取注册表信息作为回退选项的实现的名称。
     * 对于需要对注册表信息具有额外弹性的应用程序来说，这可能是必需的，否则它就无法运行。
     */
    private String backupRegistryImpl;



    private String registryRefreshSingleVipAddress;

    /**
     * heartbeatExecutor 用于初始化的线程池大小。
     */
    private int heartbeatExecutorThreadPoolSize = 2;

    /**
     *心跳执行器指数回退相关属性。它是重试延迟的最大乘数，以防发生一系列超时。
     */
    private int heartbeatExecutorExponentialBackOffBound = 10;

    /**
     * cacheRefreshExecutor 用于初始化的线程池大小。
     */
    private int cacheRefreshExecutorThreadPoolSize = 2;

    /**
     缓存刷新执行器指数回退相关属性。它是重试延迟的最大乘数，以防发生一系列超时
     */
    private int cacheRefreshExecutorExponentialBackOffBound = 10;


}
