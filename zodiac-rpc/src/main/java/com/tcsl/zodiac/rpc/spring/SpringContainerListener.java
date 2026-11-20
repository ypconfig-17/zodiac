package com.tcsl.zodiac.rpc.spring;

import com.tcsl.zodiac.rpc.core.ConnectContext;
import com.tcsl.zodiac.rpc.core.ServiceContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

/**
 * @className: SpringContainerListener
 * @description:
 * @author: yang pan
 */
public class SpringContainerListener implements ApplicationListener {
    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ContextClosedEvent){
//            ServiceContext.shutDown();
//            ConnectContext.shutDown();
        }
    }
}
