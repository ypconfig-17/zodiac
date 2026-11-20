package com.tcsl.zodiac.cilent.cilent;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tcsl.zodiac.cilent.core.InstanceDegisterListener;
import com.tcsl.zodiac.cilent.core.InstanceListener;
import com.tcsl.zodiac.cilent.core.InstanceRegisterListener;
import com.tcsl.zodiac.cilent.http.RestTemplateFactory;
import com.tcsl.zodiac.cilent.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @className: RestCaprinornCilentRegister
 * @description:
 * @author: yang pan
 */
public class ZodiacDiscoveryRegister implements ServiceRegister {
    protected final Logger logger = LoggerFactory.getLogger(ZodiacDiscoveryRegister.class);
    private AtomicReference<InstanceStatus> status = new AtomicReference<>(InstanceStatus.UNKNOWN);
    private AtomicBoolean isRuning = new AtomicBoolean(false);
    private List<InstanceListener> listeners = new ArrayList<>();
    private final ScheduledExecutorService scheduler;
    private final ThreadPoolExecutor registerExecutor;
    private final ThreadPoolExecutor heartbeatExecutor;
    private final ThreadPoolExecutor cacheRefreshExecutor;
    private final InstanceInfo instanceInfo;
    private ZodiacRestRegister delegate;

    private RestTemplate restTemplate;
    private BeatHeartTask beatHeartTask;


    @Override
    public InstanceInfo getInstanceInfo() {
        return instanceInfo;
    }


    public ZodiacDiscoveryRegister(Properties properties) {
        this.instanceInfo = initInstanceInfo(properties);

        restTemplate = RestTemplateFactory.create();
        delegate = new ZodiacRestRegister(restTemplate, this.getInstanceInfo());
        this.scheduler = Executors.newScheduledThreadPool(2,
                new ThreadFactoryBuilder().setNameFormat("ZodiacDiscoveryRegister")
                        .setDaemon(true)
                        .build());

        this.registerExecutor = new ThreadPoolExecutor(
                1, this.instanceInfo.getHeartbeatExecutorThreadPoolSize(), 0, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new ThreadFactoryBuilder()
                        .setNameFormat("ZodiacDiscoveryRegister-Service-%d")
                        .setDaemon(true)
                        .build());
        this.heartbeatExecutor = new ThreadPoolExecutor(
                1, this.instanceInfo.getHeartbeatExecutorThreadPoolSize(), 0, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new ThreadFactoryBuilder()
                        .setNameFormat("ZodiacDiscoveryRegister-HeartbeatExecutor-%d")
                        .setDaemon(true)
                        .build());

        this.cacheRefreshExecutor = new ThreadPoolExecutor(
                1, this.instanceInfo.getCacheRefreshExecutorThreadPoolSize(), 0, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new ThreadFactoryBuilder()
                        .setNameFormat("ZodiacDiscoveryRegister-CacheRefreshExecutor-%d")
                        .setDaemon(true)
                        .build()
        );
        long renewalIntervalInSecs = this.instanceInfo.getRenewalintervalinsecs();
        int expBackOffBound = this.instanceInfo.getHeartbeatExecutorExponentialBackOffBound();
        this.beatHeartTask = new BeatHeartTask(scheduler, heartbeatExecutor, renewalIntervalInSecs, expBackOffBound, this::renew);
        this.scheduler.schedule(beatHeartTask, 0, TimeUnit.MILLISECONDS);
        listeners.add(new InstanceRegisterListener());
        listeners.add(new InstanceDegisterListener());
    }

    private InstanceInfo initInstanceInfo(Properties properties) {
        InstanceInfo instanceInfo = new InstanceInfo();
        String namespace = properties.getProperty("namespace");
        if (StringUtils.isNotEmpty(namespace)) {
            namespace = "default";
        }
        instanceInfo.setNameSpace(namespace);
        String serviceUrl = properties.getProperty("serverAddr");
        instanceInfo.setServiceUrl(serviceUrl);
        String[] split = serviceUrl.split(",");
        instanceInfo.setServerList(Arrays.asList(split));
        instanceInfo.setAppName(properties.getProperty("service"));
        instanceInfo.setIp(properties.getProperty("hostName"));
        if (StringUtils.isEmpty(instanceInfo.getInstanceId())){
            instanceInfo.setInstanceId(instanceInfo.getIp());
        }
        return instanceInfo;
    }


    @Override
    public void register() {
        if (isRuning.compareAndSet(isRuning.get(),true)){
            delegate.register();
        }

    }


    @Override
    public void deregister() {
        delegate.deregister();

    }

    @Override
    public InstanceInfo getInstance(String appName, String instanceId) {
        return delegate.getInstance(appName, instanceId);
    }

    @Override
    public List<InstanceInfo> getInstances(String appName) {
        return delegate.getInstances(appName);
    }

    @Override
    public List<String> getServices() {
        return delegate.getServices();
    }


    @Override
    public boolean renew() {
        return delegate.renew();
    }

    private void shutdown() {
        isRuning.compareAndSet(true,false);

    }


    public void setPort(int port) {
        this.instanceInfo.setPort(port);
    }
}
