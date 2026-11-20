package com.tcsl.zodiac.server.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @className: Instance
 * @description:
 * @author: yang pan
 */
@Data
public class Instance implements Serializable {


    private static final long serialVersionUID = -1531355690926825684L;
    /**
     * 服务名称
     */
    private String appName;
    /**
     * 实例id
     */
    private String instanceId;
    /**
     * 实例ip
     */
    private String ip;

    /**
     * 实例port
     */
    private Integer port;

    /**
     * 实例权重
     */
    private double weight = 1.0D;

    /**
     * 实例健康状态
     */
    private AtomicBoolean healthy = new AtomicBoolean(true);

    /**
     * If instance is enabled to accept request.
     */
    private boolean enabled = true;

    /**
     * 瞬时
     */
    private boolean ephemeral = true;


    private Long lastUpdatedTimestamp ;

    /**
     *扩展信息
     */
    private Map<String, String> metadata = new HashMap<>();

    public Instance() {
    }
}

