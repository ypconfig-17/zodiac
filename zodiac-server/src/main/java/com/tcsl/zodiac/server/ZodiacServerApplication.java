package com.tcsl.zodiac.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * @className: ZodiacServerApplication
 * @description:
 * @author: yang pan
 */
@SpringBootApplication
@Slf4j
public class ZodiacServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZodiacServerApplication.class,args);
    }

}

