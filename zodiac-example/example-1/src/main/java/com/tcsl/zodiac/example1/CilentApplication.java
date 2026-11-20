package com.tcsl.zodiac.example1;

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
public class CilentApplication {

    public static void main(String[] args) {
        SpringApplication.run(CilentApplication.class);
    }
}
