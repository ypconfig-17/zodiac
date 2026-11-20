package com.tcsl.zodiac.cilent.register;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @className: CapricornServiceRegister
 * @description:
 * @author: yang pan
 */
public class ZodiacAutoServiceRegistration implements AutoServiceRegistration, SmartLifecycle, Ordered, SmartApplicationListener {

    private static final Logger log = LoggerFactory.getLogger(ZodiacAutoServiceRegistration.class);

    private AtomicBoolean running = new AtomicBoolean(false);
    private ZodiacRegistration registration;
    private ZodiacServiceRegister serviceRegistry;
    private ApplicationContext context;
    private AtomicInteger port = new AtomicInteger(0);
    private int order = 0;



    public ZodiacAutoServiceRegistration(ZodiacServiceRegister serviceRegistry, ZodiacRegistration registration, ApplicationContext context) {
        this.registration = registration;
        this.serviceRegistry = serviceRegistry;
        this.context = context;
    }
    public AtomicBoolean getRunning() {
        return running;
    }


    @Override
    public void start() {
        // only set the port  this.port != 0
        if (this.registration.getPort() <= 0 ) {
            this.registration.setPort(this.port.get());
        }
        log.warn("the properties has not  port:{}",port);
        if (this.registration.getPort() > 0){
            if (this.getRunning().compareAndSet(running.get(),true) ) {
            this.serviceRegistry.register(this.registration);
                this.context.publishEvent(new InstanceRegisteredEvent<>(this, this.registration.getZodiacDiscoverProperties()));
                this.running.compareAndSet(false,true);
            }
        }

    }

    @Override
    public void stop() {
        if (this.getRunning().compareAndSet(true, false) ) {
            this.serviceRegistry.deregister(this.registration);
        }
        this.serviceRegistry.close();
    }
    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public boolean isRunning() {
        return this.running.get();
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return WebServerInitializedEvent.class.isAssignableFrom(eventType)
                || ContextClosedEvent.class.isAssignableFrom(eventType);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof WebServerInitializedEvent) {
            onApplicationEvent((WebServerInitializedEvent) event);
        }
        else if (event instanceof ContextClosedEvent) {
            onApplicationEvent((ContextClosedEvent) event);
        }
    }
    public void onApplicationEvent(WebServerInitializedEvent event) {
        String contextName = event.getApplicationContext().getServerNamespace();
        if (contextName == null || !contextName.equals("management")) {
            int localPort = event.getWebServer().getPort();
            if (this.port.get() == 0) {
                log.info("Updating port to " + localPort);
                this.port.compareAndSet(0, localPort);
                start();
            }
        }
    }
    public void onApplicationEvent(ContextClosedEvent event) {
        if (event.getApplicationContext() == context) {
            stop();
        }
    }


    @Override
    public int getPhase() {
        return 0;
    }


    @Override
    public boolean isAutoStartup() {
        return true;
    }
}
