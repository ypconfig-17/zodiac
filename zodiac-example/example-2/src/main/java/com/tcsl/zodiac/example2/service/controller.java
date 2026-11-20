package com.tcsl.zodiac.example2.service;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @program: zodiac
 * @description:
 * @author: Yang Pan
 */
@Component
public class controller implements EnvironmentAware {
    Environment environment;
    @Override
    public void setEnvironment(Environment environment) {
        this. environment = environment;
    }
}

