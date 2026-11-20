package com.tcsl.zodiac.example2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
*@className: ServerApplication
*@description: 
*@author: yang pan
*/
@SpringBootApplication
@ComponentScan("com.tcsl.zodiac")
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class);
    }
}
