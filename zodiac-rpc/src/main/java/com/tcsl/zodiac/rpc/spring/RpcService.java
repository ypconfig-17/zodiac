package com.tcsl.zodiac.rpc.spring;

import java.lang.annotation.*;

/**
 * @className: RpcReference
 * @description:
 * @author: yang pan
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RpcService {

    String version() default "";



    String name() default "";


}
