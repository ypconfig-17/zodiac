package com.tcsl.zodiac.rpc.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * @className: RpcInjectBeanPostProcessorAdapter
 * @description: 处理自动注入注解
 * @author: yang pan
 */
public class RpcInjectBeanPostProcessor  implements InstantiationAwareBeanPostProcessor,
        MergedBeanDefinitionPostProcessor, ApplicationContextAware, BeanClassLoaderAware,Serializable {
    private static final Logger logger = LoggerFactory.getLogger(RpcInjectBeanPostProcessor.class);

    private  ApplicationContext applicationContext;

    private  ClassLoader classLoader;

    private final ConcurrentMap<String, RpcReferenceBean> referenceBeansCache = new ConcurrentHashMap<>(256);

    private final transient Map<String, InjectionMetadata> injectionMetadataCache = new ConcurrentHashMap<>(256);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        InjectionMetadata metadata = this.findReferenceMetadata(beanName, bean.getClass(), pvs);

        try {
            metadata.inject(bean, beanName, pvs);
            return pvs;
        } catch (Throwable throwable) {
            throw new BeanCreationException(beanName, "Injection of resource dependencies failed", throwable);
        }
    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        if (beanType != null) {
            InjectionMetadata metadata = findReferenceMetadata(beanName, beanType, null);
            metadata.checkConfigMembers(beanDefinition);
        }
    }



    private InjectionMetadata findReferenceMetadata(String beanName, Class<?> clazz, PropertyValues pvs) {
        String cacheKey = StringUtils.hasLength(beanName) ? beanName : clazz.getName();
        InjectionMetadata metadata = (InjectionMetadata)this.injectionMetadataCache.get(cacheKey);
        if (InjectionMetadata.needsRefresh(metadata, clazz)) {
            synchronized(this.injectionMetadataCache) {
                metadata = (InjectionMetadata)this.injectionMetadataCache.get(cacheKey);
                if (InjectionMetadata.needsRefresh(metadata, clazz)) {
                    if (metadata != null) {
                        metadata.clear(pvs);
                    }

                    metadata = this.buildResourceMetadata(clazz);
                    this.injectionMetadataCache.put(cacheKey, metadata);
                }
            }
        }

        return metadata;
    }

    private InjectionMetadata buildResourceMetadata(Class<?> clazz) {
        if (!AnnotationUtils.isCandidateClass(clazz, RpcReference.class)) {
            return InjectionMetadata.EMPTY;
        } else {
            List<InjectionMetadata.InjectedElement> elements = new ArrayList();
            Class targetClass = clazz;

            do {
                List<InjectionMetadata.InjectedElement> currElements = new ArrayList();
                ReflectionUtils.doWithLocalFields(targetClass, (field) -> {
                    RpcReference ann = field.getAnnotation(RpcReference.class);
                    if (ann != null) {
                        if (Modifier.isStatic(field.getModifiers())) {
                            if (logger.isInfoEnabled()) {
                                logger.info("rpc inject annotation is not supported on static fields: " + field);
                            }
                            return;
                        }
                        currElements.add(new InjectFieldElement(field, ann));
                    }
                });
                elements.addAll(0, currElements);
                targetClass = targetClass.getSuperclass();
            } while(targetClass != null && targetClass != Object.class);

            return InjectionMetadata.forElements(elements, clazz);
        }
    }





    private class InjectFieldElement extends  InjectionMetadata.InjectedElement {

        private volatile boolean cached;
        @Nullable
        private RpcReference rpcReference;

        public InjectFieldElement(Field field, RpcReference rpcReference) {
            super(field, null);
            this.rpcReference = rpcReference;
        }

        @Override
        protected void inject(Object bean, @Nullable String beanName, @Nullable PropertyValues pvs) throws Throwable {
            Field field = (Field) this.member;
            Class<?> referenceClass = field.getType();
            Object value = buildReferenceBean(rpcReference,referenceClass);
            if (value != null) {
                ReflectionUtils.makeAccessible(field);
                field.set(bean, value);
            }
        }

    }



    private Object buildReferenceBean(RpcReference reference,  Class<?> referenceClass) throws Exception {
        String referenceBeanCacheKey = generateReferenceBeanCacheKey(reference, referenceClass);

        RpcReferenceBean rpcReferenceBean = referenceBeansCache.get(referenceBeanCacheKey);

        if (rpcReferenceBean == null) {
            rpcReferenceBean = new RpcReferenceBean(reference,referenceClass);
            rpcReferenceBean.setApplicationContext(applicationContext);
            rpcReferenceBean.afterPropertiesSet();
            referenceBeansCache.putIfAbsent(referenceBeanCacheKey, rpcReferenceBean);

        }

        return rpcReferenceBean.getObject();
    }

    private String generateReferenceBeanCacheKey(RpcReference reference,  Class<?> referenceClass) {
        String interfaceName = referenceClass.getName();;
        return interfaceName + ":" + reference.version();
    }


}
