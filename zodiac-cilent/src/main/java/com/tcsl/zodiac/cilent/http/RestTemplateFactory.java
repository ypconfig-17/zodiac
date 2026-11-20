package com.tcsl.zodiac.cilent.http;

import org.springframework.web.client.RestTemplate;

/**
 * @className: RestTemplateFactory
 * @description:
 * @author: yang pan
 */
public class RestTemplateFactory {




   public static RestTemplate create(){
       RestTemplate restTemplate = new RestTemplate();


       return restTemplate;
   }
}
