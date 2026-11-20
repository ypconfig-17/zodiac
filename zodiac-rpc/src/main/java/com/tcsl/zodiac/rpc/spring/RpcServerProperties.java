package com.tcsl.zodiac.rpc.spring;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @program: zodiac
 * @description:
 * @author: Yang Pan
 */
@Configuration
@ConfigurationProperties(prefix = "zodiac.rpc")
@Data
public class RpcServerProperties {

    int port = 25500;

}

