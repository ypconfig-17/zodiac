package com.tcsl.zodiac.server.register;

import com.tcsl.zodiac.server.model.AppInfo;
import com.tcsl.zodiac.server.model.Instance;

import java.util.List;

/**
 * @className: InstanceRegister
 * @description:
 * @author: yang pan
 */
public interface InstanceRegister {
    /**
     * 获取服务列表
     * @return List<com.tcsl.zodiac.server.model.Instance>
     * @author yangpan
     */
    List<AppInfo> getAppInfos();
    /**
     * 获取服务列表
     * @return List<com.tcsl.zodiac.server.model.Instance>
     * @author yangpan
     */
    List<Instance> getInstacnes();
    /**
     * 获取服务列表
     * @return List<com.tcsl.zodiac.server.model.Instance>
     * @author yangpan
     */
    List<Instance> getInstacnes(String appName);
    /**
     * 获取服务信息
     * @param serverName
     * @param id
     * @return
     */
    Instance getInstacne(String serverName, String id);
    /**
     * 注册服务
     * @param instance
     * @author yangpan
     */
    void register(Instance instance);
    /**
     * 下线服务
     * @param appName
     * @param instanceId
     * @author yangpan
     */
    void deregister(String appName, String instanceId);


}

