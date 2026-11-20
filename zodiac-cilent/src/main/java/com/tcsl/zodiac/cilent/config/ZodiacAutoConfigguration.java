package com.tcsl.zodiac.cilent.config;

import com.tcsl.zodiac.cilent.cilent.ZodiacDiscoveryRegister;
import com.tcsl.zodiac.cilent.discovery.ZodiacDiscoveryCilent;
import com.tcsl.zodiac.cilent.register.ZodiacAutoServiceRegistration;
import com.tcsl.zodiac.cilent.register.ZodiacRegistration;
import com.tcsl.zodiac.cilent.register.ZodiacServiceRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.CommonsClientAutoConfiguration;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.cloud.client.serviceregistry.ServiceRegistryAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @className: CapricornServiceRegister
 * @description:
 * @author: yang pan
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties
@ConditionalOnClass(ZodiacDiscoverProperties.class)
@ConditionalOnDiscoveryEnabled
@AutoConfigureBefore({ CommonsClientAutoConfiguration.class, ServiceRegistryAutoConfiguration.class })
@AutoConfigureAfter(name = {
        "org.springframework.cloud.autoconfigure.RefreshAutoConfiguration",
        "org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationAutoConfiguration" })
public class ZodiacAutoConfigguration {

    private ConfigurableEnvironment env;

    public ZodiacAutoConfigguration(ConfigurableEnvironment env) {
        this.env = env;
    }


    @Bean
    public ZodiacServiceRegister zodiacServiceRegister() {
        return new ZodiacServiceRegister();
    }



    @Bean
//    @ConditionalOnBean(AutoServiceRegistrationProperties.class)
    @ConditionalOnProperty(value = "spring.cloud.service-registry.auto-registration.enabled", matchIfMissing = true)
    public ZodiacAutoServiceRegistration zodiacAutoServiceRegistration(ZodiacServiceRegister zodiacServiceRegister,
                                                                       ZodiacRegistration registration,
                                                                       ApplicationContext context) {
        return new ZodiacAutoServiceRegistration( zodiacServiceRegister, registration,context);
    }

    @Configuration(proxyBeanMethods = false)
    protected static class ZodiacClientConfiguration {

        @Autowired
        private ApplicationContext context;

        @Bean(destroyMethod = "shutdown")
        @ConditionalOnMissingBean(value = ZodiacDiscoveryRegister.class, search = SearchStrategy.CURRENT)
        public ZodiacDiscoveryRegister caprinornCilentRegister(ZodiacDiscoverProperties zodiacDiscoverProperties) {
           return new ZodiacDiscoveryRegister(zodiacDiscoverProperties.getProperties());
        }

        @Bean
//        @ConditionalOnBean(AutoServiceRegistrationProperties.class)
        @ConditionalOnProperty(value = "spring.cloud.service-registry.auto-registration.enabled", matchIfMissing = true)
        public ZodiacRegistration capricornRegistration(ZodiacDiscoverProperties zodiacDiscoverProperties, ZodiacDiscoveryRegister zodiacCilentRegister) {
            return new ZodiacRegistration(zodiacDiscoverProperties, zodiacCilentRegister);
        }

        @Bean
        public ZodiacDiscoveryCilent zodiacDiscoveryCilent(ZodiacDiscoveryRegister zodiacDiscoveryRegister) {
            return new ZodiacDiscoveryCilent(zodiacDiscoveryRegister);
        }
    }

}
