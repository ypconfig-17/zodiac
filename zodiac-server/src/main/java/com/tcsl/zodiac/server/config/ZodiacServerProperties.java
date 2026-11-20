package com.tcsl.zodiac.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @className: ServerProperties
 * @description:
 * @author: yang pan
 */
@Configuration
@Data
@ConfigurationProperties("zodiac.register.server")
public class ZodiacServerProperties {

   public static final String DEFAULT_GOURP = "zodiac";

   private String dataPath = "E:\\TEST\\node1";

   private String startUpModel = "cluster";

   private String groupId = DEFAULT_GOURP;

   private String serverAddress = "127.0.0.1:8081";

   private String initialServers = "127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083";

   private  int instanceDeleteIntervalSeconds = 30;

   private  int instanceHealthLookUp = 10;

   private  int instanceUnHealthIntervalSeconds = 20;

   private  int jraftExecutorThreadPoolSize = 10;

}

