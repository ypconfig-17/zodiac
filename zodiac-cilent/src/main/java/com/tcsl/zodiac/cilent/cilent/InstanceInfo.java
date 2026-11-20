package com.tcsl.zodiac.cilent.cilent;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @className: InstanceInfo
 * @description:
 * @author: yang pan
 */
@Data
public class InstanceInfo {

    private volatile String serviceUrl ;

    private volatile List<String> serverList ;

    private volatile String nameSpace;

    private volatile String appName;


    private volatile String instanceId ;

    private volatile String ip;

    private volatile int port;

    private volatile String appPathIdentifier;

    private volatile Map<String, String> metadata;

    private volatile InstanceStatus status = InstanceStatus.UP;

    private volatile Long lastUpdatedTimestamp;

    private volatile Long lastDirtyTimestamp = 3000L;

    private volatile Long renewalintervalinsecs = 3000L;


    private volatile Long renewalIntervalInSecs = 1000L;


    private volatile int heartbeatExecutorExponentialBackOffBound = 15 ;
    private volatile int heartbeatExecutorThreadPoolSize = 2;
    private volatile int cacheRefreshExecutorThreadPoolSize = 2;

    public InstanceInfo() {
    }

    public String getServiceUrl() {
        if (serviceUrl.endsWith("/")){
            return serviceUrl + "apps/";
        }else{
            return serviceUrl + "/apps/";
        }

    }

    public String getAppPathIdentifier() {
        return this.getAppName() + "/" + this.getInstanceId();
    }
}
