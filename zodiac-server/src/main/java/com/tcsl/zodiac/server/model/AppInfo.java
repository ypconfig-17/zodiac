package com.tcsl.zodiac.server.model;

import lombok.Data;


/**
 * @className: AppInfo
 * @description:
 * @author: yang pan
 */
@Data
public class AppInfo {

    private String appName;

    private String groupName;

    private long instanceCount;

    private long healthInstanceCount;

}
