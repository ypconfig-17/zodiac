package com.tcsl.zodiac.cilent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ZodiaCilentApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZodiaCilentApplication.class, args);
    }

}
