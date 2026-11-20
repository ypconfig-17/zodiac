package com.tcsl.zodiac.server.register;

import com.tcsl.zodiac.server.config.ZodiacServerProperties;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

/**
 * @className: RegisterFactory
 * @description:
 * @author: yang pan
 */
@Component
public class RegisterFactory {

    private InstanceRegister instanceRegister = null;

    public InstanceRegister getRegister() {
        return instanceRegister;
    }



    @SneakyThrows
    public synchronized void  createRegister(boolean isStandAlone, ZodiacServerProperties properties){
        if (isStandAlone){
            StandaloneInstanceRegister instanceRegister = new StandaloneInstanceRegister();
            instanceRegister.init(properties);
            this.instanceRegister =instanceRegister;
        }else {
            ClusterInstanceRegister instanceRegister =  new ClusterInstanceRegister();
            instanceRegister.init(properties);
            instanceRegister.afterPropertiesSet();
            this.instanceRegister =instanceRegister;
        }

    }
}
