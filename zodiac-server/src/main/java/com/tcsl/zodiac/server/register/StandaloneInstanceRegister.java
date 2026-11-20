package com.tcsl.zodiac.server.register;

import com.tcsl.zodiac.server.config.ZodiacServerProperties;
import com.tcsl.zodiac.server.model.AppInfo;
import com.tcsl.zodiac.server.model.Instance;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * @className: StandaloneInstanceRegister
 * @description:
 * @author: yang pan
 */
@Slf4j
public class StandaloneInstanceRegister extends AbstractInstanceRegister {
    /**
     * 服务存储
     */
    ConcurrentMap<String, Map<String, Instance>> resposiry = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock read = readWriteLock.readLock();
    private final Lock write = readWriteLock.writeLock();

    public void init(ZodiacServerProperties serverProperties) {
        super.initScheduler();

        scheduler.scheduleWithFixedDelay(() -> {
            log.info("仓储定时任务开始执行。");
            Iterator<Map<String, Instance>> iterator = resposiry.values().iterator();
            while (iterator.hasNext()) {
                Map<String, Instance> instanceMap = iterator.next();
                if (MapUtils.isNotEmpty(instanceMap)) {
                    instanceMap.values().forEach(instance -> {
                        if (System.currentTimeMillis() - instance.getLastUpdatedTimestamp()>serverProperties.getInstanceUnHealthIntervalSeconds()){
                            instance.setHealthy(new AtomicBoolean(false));
                        }
                    });
                    instanceMap.values().removeIf(instance ->
                            System.currentTimeMillis() - instance.getLastUpdatedTimestamp() > serverProperties.getInstanceDeleteIntervalSeconds());
                }
                if (instanceMap.isEmpty()) {
                    iterator.remove();
                }
            }
        }, 5,serverProperties.getInstanceHealthLookUp(), TimeUnit.SECONDS);

    }

    @Override
    public List<AppInfo> getAppInfos() {
        List<AppInfo> result = new ArrayList<>();
        Set<String> appNames = resposiry.keySet();
        for (String appName : appNames) {
            AppInfo appInfo = new AppInfo();
            Map<String, Instance> instanceMap = resposiry.get(appName);
            appInfo.setAppName(appName);
            appInfo.setGroupName("default");
            appInfo.setInstanceCount(instanceMap.keySet().size());
            appInfo.setHealthInstanceCount(instanceMap.keySet().size());
            result.add(appInfo);
        }
        return result;
    }

    @Override
    public List<Instance> getInstacnes() {
        List<Instance> result = new ArrayList<>();
        try {

            Collection<Map<String, Instance>> values = resposiry.values();
            List<Map<String, Instance>> maps = new ArrayList<>(values);
            if (CollectionUtils.isNotEmpty(maps)) {
                List<Instance> list = maps.stream()
                        .map(Map::values)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
                result.addAll(list);
            }
        } catch (Exception e) {
            log.error("获取服务实例失败", e);
        }
        return result;
    }

    @Override
    public List<Instance> getInstacnes(String appName) {
        List<Instance> result = new ArrayList<>();
        try {
            Set<String> appNames = resposiry.keySet();
            if (CollectionUtils.isNotEmpty(appNames)) {

                for (String registedAppName : appNames){
                    if (appName.equals(registedAppName)){
                        Map<String, Instance> instanceMap = resposiry.get(appName);
                        Collection<Instance> values = instanceMap.values();
                        result.addAll(values);
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取服务实例失败", e);
        }
        return result;
    }

    @Override
    public Instance getInstacne(String serverName, String instanceId) {
        try {
            Map<String, Instance> instances = resposiry.get(serverName);
            if (MapUtils.isNotEmpty(instances)) {
                Instance instance = instances.get(instanceId);
                return instance;
            }
        } catch (Exception e) {
            log.error("获取服务实例失败", e);
        }
        return null;
    }

    @Override
    public void register(Instance instance) {
        write.lock();
        try {
            resposiry.computeIfAbsent(instance.getAppName(), s -> new HashMap<>()).put(instance.getInstanceId(), instance);
        } catch (Exception e) {
            log.error("注册服务实例失败", e);
        } finally {
            write.unlock();
        }

    }

    @Override
    public void deregister(String appName, String instanceId) {
        write.lock();
        try {
            Map<String, Instance> instanceMap = resposiry.get(appName);
            if (MapUtils.isNotEmpty(instanceMap)) {
                resposiry.remove(instanceId);
            }
        } catch (Exception e) {
            log.error("注销服务实例失败", e);
        } finally {
            write.unlock();
        }
    }
}

