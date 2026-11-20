package com.tcsl.zodiac.rpc.spring;

import com.tcsl.zodiac.rpc.core.ServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.*;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;
import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;
import static org.springframework.util.ClassUtils.getAllInterfacesForClass;
import static org.springframework.util.ClassUtils.resolveClassName;
import static org.springframework.util.StringUtils.hasText;

/**
 * @className: RpcServiceBeanPostProcessor
 * @description:
 * @author: yang pan
 */
public class RpcServiceBeanPostProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware, ResourceLoaderAware, BeanClassLoaderAware , SmartInitializingSingleton {

    private static Logger logger = LoggerFactory.getLogger(RpcServiceBeanPostProcessor.class);

    private ClassLoader classLoader;
    private Environment environment;
    private ResourceLoader resourceLoader;
    private  Set<String> packages = new HashSet<>();
    ConfigurableListableBeanFactory beanFactory;
    public RpcServiceBeanPostProcessor() {
    }

    public RpcServiceBeanPostProcessor(Set<String> packages) {
        this.packages = packages;
    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory=beanFactory;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        ClassPathScanningCandidateComponentProvider scanner = getScanner();

        scanner.setEnvironment(environment);
        scanner.setResourceLoader(resourceLoader);
        scanner.addIncludeFilter(new AnnotationTypeFilter(RpcService.class));

        Set<String> basePackages = getBasePackages();

        LinkedHashSet<BeanDefinition> candidateServiceBean = new LinkedHashSet<>();

        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
            candidateServiceBean.addAll(candidateComponents);
        }

        if (CollectionUtils.isEmpty(candidateServiceBean)) {
            logger.warn("There is no @RpcService annotation in this basePackages or base packages like:{}", basePackages);
        }

        for (BeanDefinition beanDefinition : candidateServiceBean) {
            if (beanDefinition instanceof AnnotatedBeanDefinition) {
                AnnotatedBeanDefinition annotatedBeanDefinition = (AnnotatedBeanDefinition) beanDefinition;
                AnnotationMetadata annotationMetadata = annotatedBeanDefinition.getMetadata();

                Map<String, Object> attributes = annotationMetadata
                        .getAnnotationAttributes(RpcService.class.getCanonicalName());
                Assert.isTrue(!annotationMetadata.isInterface(), "@RpcService can not specified on an interface");
                registerRpcSeviceBeans(annotationMetadata, registry,attributes);
            }
        }


    }

    private void registerRpcSeviceBeans(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry, Map<String, Object> attributes) {

        String beanClassName = annotationMetadata.getClassName();
        Class clazz = ClassUtils.resolveClassName(beanClassName, null);
        Class<?> interfaceClass = resolveServiceInterfaceClass( clazz);
        BeanDefinitionBuilder beanDefinition = rootBeanDefinition(clazz);
        registry.registerBeanDefinition(interfaceClass.getName(),beanDefinition.getBeanDefinition());
        ServiceContext.loadServiceName(interfaceClass.getName());
    }
    @Override
    public void afterSingletonsInstantiated() {
        Set<String> serviceNames = ServiceContext.getServiceNames();
        for (String serviceName :serviceNames){
            Object bean = this.beanFactory.getBean(serviceName);
            ServiceContext.loadService(serviceName,bean);
        }
    }
    private Annotation findServiceAnnotation(Class<?> beanClass) {
        Annotation service = findMergedAnnotation(beanClass, RpcService.class);
        if (service == null) {
            service = findMergedAnnotation(beanClass,RpcService.class);
        }
        return service;
    }
    public static Class<?> resolveServiceInterfaceClass( Class<?> defaultInterfaceClass)throws IllegalArgumentException {

        ClassLoader classLoader = defaultInterfaceClass != null ? defaultInterfaceClass.getClassLoader() : Thread.currentThread().getContextClassLoader();
        Class<?> interfaceClass =null;

        if ( defaultInterfaceClass != null) {

            Class<?>[] allInterfaces = ClassUtils.getAllInterfacesForClass(defaultInterfaceClass);

            if (allInterfaces.length > 0) {
                interfaceClass = allInterfaces[0];
            }
        }

        Assert.notNull(interfaceClass,
                "@RpcService interfaceClass() or interfaceName() or interface class must be present!");

        Assert.isTrue(interfaceClass.isInterface(),
                "The annotated type must be an interface!");

        return interfaceClass;
    }

    public static <T> T getAttribute(AnnotationAttributes attributes, String name) {
        return (T) attributes.get(name);
    }

    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (!beanDefinition.getMetadata().isAnnotation()) {
                        isCandidate = true;
                    }
                }
                return isCandidate;
            }
        };
    }

    protected Set<String> getBasePackages() {
        Set<String> basePackages = this.packages;
        if (basePackages.isEmpty()) {

            basePackages.add("com.tcsl.zodiac");
        }
        return basePackages;
    }



}
