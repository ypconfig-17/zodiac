package com.tcsl.zodiac.rpc.spring;

import com.tcsl.zodiac.rpc.spring.loadbalance.LoadBalance;

import java.lang.annotation.*;

/**
 * @className: RpcReference
 * @description:
 * @author: yang pan
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RpcReference {

    String serverUrl() ;

    String version() default "";

    long timeout() default 1000;

    String address() default "";

    LoadBalance loadBalncer() default LoadBalance.RANDOM;
}
