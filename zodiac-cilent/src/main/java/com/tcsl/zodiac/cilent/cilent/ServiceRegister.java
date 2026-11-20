package com.tcsl.zodiac.cilent.cilent;

import com.tcsl.zodiac.cilent.http.HttpResponse;

import java.util.List;

/**
 * @className: CaprincornCilentRegister
 * @description: 服务注册者,当前服务cilent注册接口
 * @author: yang pan
 */
public interface ServiceRegister {
    /**
     * 获取注册实例元数据
     */
    InstanceInfo getInstanceInfo();
    /**
     * 注册
     */
    void register();

    /**
     * 下线
     */
    void deregister();

    /**
     * 获取实例信息
     * @param appName
     * @param instanceId
     */
    InstanceInfo getInstance(String appName, String instanceId);

    /**
     * 获取实例信息
     * @param appName
     */
    List<InstanceInfo> getInstances(String appName);

    /**
     * 获取实例信息
     */
    List<String> getServices();
    /**
     * 续期
     */
    boolean renew();


}
