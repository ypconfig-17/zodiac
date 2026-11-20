package com.tcsl.zodiac.server.register;

import com.tcsl.zodiac.server.config.ZodiacServerProperties;
import jdk.nashorn.internal.runtime.regexp.joni.constants.OPCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import java.util.List;
import java.util.Set;

/**
 * @className: RegisterLauncher
 * @description:
 * @author: yang pan
 */
@Component
@Slf4j
public class RegisterLauncher  implements ApplicationRunner {



    @Autowired
    private RegisterFactory registerFactory;
    @Autowired
    private ZodiacServerProperties properties;

    private  static final String STARTUP_MODEL_ARGUMENTS = "m";

    private  static final String STANDALONE_MODEL = "standalone";
    private  static final String DATAPATH = "dataPath";
    private  static final String SERVERADDRESS = "serverAddress";
    private  static final String INITIALSERVERS = "initialServers";

    private  boolean  isStandAlone = false;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        List<String> nonOptionArgs = args.getNonOptionArgs();
        log.info("注册中心启动参数:{}",nonOptionArgs);
        Set<String> optionNames = args.getOptionNames();
        if (optionNames.contains(STARTUP_MODEL_ARGUMENTS)){
            List<String> optionValues = args.getOptionValues(STARTUP_MODEL_ARGUMENTS);
            if (optionValues.get(0).equals(STANDALONE_MODEL)){
                properties.setStartUpModel(STANDALONE_MODEL);
                isStandAlone = true;
            }
        }
        List<String> stratUpValue = args.getOptionValues(STARTUP_MODEL_ARGUMENTS);
        if (CollectionUtils.isNotEmpty(stratUpValue)){
            if (stratUpValue.get(0).equals(STANDALONE_MODEL)){
                properties.setStartUpModel(STANDALONE_MODEL);
                isStandAlone = true;
            }
        }
        List<String> dataPathValue = args.getOptionValues(DATAPATH);
        if (CollectionUtils.isNotEmpty(dataPathValue)){
            properties.setDataPath(dataPathValue.get(0));
        }
        List<String> serverAddressValue = args.getOptionValues(SERVERADDRESS);
        if (CollectionUtils.isNotEmpty(serverAddressValue)){
            properties.setServerAddress(serverAddressValue.get(0));
        }
        List<String> initialServersValue = args.getOptionValues(INITIALSERVERS);
        if (CollectionUtils.isNotEmpty(initialServersValue)){
            properties.setInitialServers(initialServersValue.get(0));
        }
        launch(isStandAlone);

    }

    private void launch(boolean isStandAlone) {
        registerFactory.createRegister(isStandAlone,properties);
    }


}
