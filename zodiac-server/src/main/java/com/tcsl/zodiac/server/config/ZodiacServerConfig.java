package com.tcsl.zodiac.server.config;

import com.tcsl.zodiac.server.core.jraft.JraftConfig;
import com.tcsl.zodiac.server.core.jraft.ZodiacJraftEngine;
import com.tcsl.zodiac.server.model.enums.ServerModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @className: RegisterServerConfig
 * @description:
 * @author: yang pan
 */
@Configuration
@ConditionalOnBean(ZodiacServerProperties.class)
public class ZodiacServerConfig {

}
