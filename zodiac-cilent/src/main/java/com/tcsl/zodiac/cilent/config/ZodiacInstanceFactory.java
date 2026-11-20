package com.tcsl.zodiac.cilent.config;

import com.tcsl.zodiac.cilent.cilent.InstanceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * @className: InstanceInfoProvider
 * @description:
 * @author: yang pan
 */
public class ZodiacInstanceFactory {

    private static final Logger logger = LoggerFactory.getLogger(ZodiacInstanceFactory.class);


    private   ZodiacCilentProperties zodiacCilentProperties ;
    private   ZodiacDiscoverProperties zodiacDiscoverProperties ;
    private   InstanceInfo instanceInfo ;

    public ZodiacInstanceFactory(ZodiacCilentProperties zodiacCilentProperties, ZodiacDiscoverProperties zodiacDiscoverProperties) {
        this.zodiacCilentProperties = zodiacCilentProperties;
        this.zodiacDiscoverProperties = zodiacDiscoverProperties;
    }




}
