package com.tcsl.zodiac.server.register;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tcsl.zodiac.server.config.ZodiacServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @className: AbstractInstanceRegister
 * @description:
 * @author: yang pan
 */
@Service

public abstract class AbstractInstanceRegister implements InstanceRegister {

    protected ScheduledExecutorService scheduler;

    public void initScheduler() {
        scheduler =  Executors.newScheduledThreadPool(2,
                new ThreadFactoryBuilder().setNameFormat("AbstractInstanceRegister")
                        .setDaemon(true)
                        .build());
    }
}


