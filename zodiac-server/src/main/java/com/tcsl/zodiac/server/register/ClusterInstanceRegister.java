package com.tcsl.zodiac.server.register;

import com.alipay.remoting.InvokeCallback;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.RpcClient;
import com.alipay.sofa.jraft.*;
import com.alipay.sofa.jraft.closure.ReadIndexClosure;
import com.alipay.sofa.jraft.conf.Configuration;
import com.alipay.sofa.jraft.core.CliServiceImpl;
import com.alipay.sofa.jraft.entity.PeerId;
import com.alipay.sofa.jraft.option.CliOptions;
import com.alipay.sofa.jraft.rpc.CliClientService;
import com.alipay.sofa.jraft.rpc.impl.AbstractBoltClientService;
import com.alipay.sofa.jraft.rpc.impl.cli.BoltCliClientService;
import com.alipay.sofa.jraft.util.BytesUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.tcsl.zodiac.comm.utils.JacksonUtils;
import com.tcsl.zodiac.comm.utils.StringUtils;
import com.tcsl.zodiac.server.config.ZodiacServerProperties;
import com.tcsl.zodiac.server.core.jraft.JraftConfig;
import com.tcsl.zodiac.server.core.jraft.JraftStoreClosure;
import com.tcsl.zodiac.server.core.jraft.ZodiacJraftEngine;
import com.tcsl.zodiac.server.core.jraft.rpc.RpcContans;
import com.tcsl.zodiac.server.core.model.request.*;
import com.tcsl.zodiac.server.core.model.response.RpcResponse;
import com.tcsl.zodiac.server.core.store.JraftCilentStroe;
import com.tcsl.zodiac.server.core.store.JraftStroe;
import com.tcsl.zodiac.server.exception.ZodiacServerException;
import com.tcsl.zodiac.server.model.AppInfo;
import com.tcsl.zodiac.server.model.Instance;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.awt.image.Kernel;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @className: ClusterInstanceRegister
 * @description:
 * @author: yang pan
 */
@Slf4j
@Data
public class ClusterInstanceRegister extends AbstractInstanceRegister implements InitializingBean, DisposableBean {

    private JraftStroe jraftStroe;
    private CliService cliService;
    private CliClientService cliClientService;
    private RpcClient rpcClient;
    private Executor executor;
    private Node node;
    private String groupId;
    private ZodiacServerProperties serverProperties;
    private static final TypeReference<Map<String, Instance>> typeReference = new TypeReference<Map<String, Instance>>() {
    };

    public void init(ZodiacServerProperties serverProperties) {
        super.initScheduler();
        JraftConfig jraftConfig = new JraftConfig(serverProperties.getDataPath(), serverProperties.getGroupId(),
                serverProperties.getServerAddress(), serverProperties.getInitialServers(), serverProperties.getJraftExecutorThreadPoolSize());
        ZodiacJraftEngine zodiacJraftEngine = new ZodiacJraftEngine(jraftConfig);
        this.groupId = jraftConfig.getGroupId();
        this.serverProperties = serverProperties;

        try {
            zodiacJraftEngine.init();
        } catch (Exception exception) {
            throw new ZodiacServerException("zodiacJraftEngine init failed");
        }
        this.jraftStroe = zodiacJraftEngine.getJraftStroe();
        this.executor = jraftConfig.getExecutor();
        this.node = zodiacJraftEngine.getNode();
        scheduler.scheduleWithFixedDelay(() -> {
            List<Instance> instacnes = getInstacnes();
            if (CollectionUtils.isNotEmpty(instacnes)) {

                instacnes.forEach(instance -> {
                    if (System.currentTimeMillis() - instance.getLastUpdatedTimestamp() > serverProperties.getInstanceUnHealthIntervalSeconds()) {
                        instance.setHealthy(new AtomicBoolean(false));
                        register(instance);
                    }
                    if (System.currentTimeMillis() - instance.getLastUpdatedTimestamp() > serverProperties.getInstanceDeleteIntervalSeconds()) {
                        instance.setHealthy(new AtomicBoolean(false));
                        register(instance);
                    }
                });

            }
        }, 100, serverProperties.getInstanceHealthLookUp(), TimeUnit.SECONDS);
    }

    @Override
    public List<AppInfo> getAppInfos() {
        List<AppInfo> result = new ArrayList<>();
        jraftStroe.allKeys(new JraftStoreClosure() {
            @Override
            public void run(Status status) {
                Set<String> appNames = (Set<String>) getData();
                for (String appName : appNames) {
                    AppInfo appInfo = new AppInfo();
                    appInfo.setAppName(appName);
                    List<Instance> instacnes = getInstacnes(appName);
                    appInfo.setGroupName("DEFAULT");
                    appInfo.setHealthInstanceCount(Lists.newArrayList(instacnes).stream().filter(ins -> ins.getHealthy().get()).count());
                    appInfo.setInstanceCount(instacnes.size());
                    result.add(appInfo);
                }
            }
        });

        return result;
    }

    @Override
    public List<Instance> getInstacnes() {
        List<Instance> result = new ArrayList<>();
        jraftStroe.getAll(new JraftStoreClosure() {
            @Override
            public void run(Status status) {
                if (status.isOk()) {
                    List<String> allValues = (List<String>) getData();
                    allValues.forEach(instancesJson -> {
                        if (StringUtils.isNotEmpty(instancesJson)){
                            Map<String, Instance> instanceMap = JacksonUtils.toObj(instancesJson, typeReference);
                            result.addAll(instanceMap.values());
                        }

                    });

                }
            }
        });
        return result;
    }

    @Override
    public List<Instance> getInstacnes(String appName) {
        Map<String, Instance> instanceMap = getInstanceMap(appName);
        return Lists.newArrayList(instanceMap.values());
    }


    private  Map<String, Instance> getInstanceMap(String appName) {
        Map<String, Instance> result = new HashMap<>();
        jraftStroe.get(appName, new JraftStoreClosure() {
            @Override
            public void run(Status status) {
                if (status.isOk()) {
                    String instancesJson = (String) getData();
                    if (StringUtils.isNotEmpty(instancesJson)){
                        Map<String, Instance> stringInstanceMap = JacksonUtils.toObj(instancesJson, typeReference);
                        result.putAll(stringInstanceMap);
                    }

                }
            }
        });
        return result;
    }

    @Override
    public Instance getInstacne(String serverName, String id) {
        List<Instance> instacnes = getInstacnes();
        for (Instance instance : instacnes) {
            if (instance.getInstanceId().equals(id)) {
                return instance;
            }
        }
        return null;
    }

    @Override
    public void register(Instance instance) {
        Map<String, Instance> instanceMap = getInstanceMap(instance.getAppName());

        if (MapUtils.isEmpty(instanceMap)) {
            instanceMap = new HashMap<>();
        }
        instanceMap.put(instance.getIp(),instance);
        PutRequest request = new PutRequest();
        request.setKey(instance.getAppName());
        request.setValue(JacksonUtils.toJson(instanceMap));
        try {
            rpcInvokeWithCallback(request, rpcResponse -> {

                if (rpcResponse.isSuccess()) {
                    log.info("服务注册成功instance:{}", instance);
                }
            });

        } catch (Exception e) {
            log.error("register instance filad instance:{}", instance);
        }
    }

    private void rpcInvokeWithCallback(RpcRequest request, Consumer<RpcResponse> consumer) throws RemotingException, InterruptedException, TimeoutException {
        final InvokeCallback invokeCallback = new InvokeCallback() {
            @Override
            public void onResponse(final Object result) {
                log.info("收到请求响应:{}", result);
                final RpcResponse response = (RpcResponse) result;
                consumer.accept(response);
            }

            @Override
            public void onException(final Throwable t) {
                throw new RuntimeException(t);
            }

            @Override
            public Executor getExecutor() {
                return executor;
            }

        };


        Configuration conf = new Configuration();
        conf.parse(serverProperties.getInitialServers());
        RouteTable.getInstance().updateConfiguration(groupId, conf);
        if (!RouteTable.getInstance().refreshLeader(cliClientService, groupId, 1000).isOk()) {
            throw new IllegalStateException("Refresh leader failed");
        } else {
            PeerId leader = RouteTable.getInstance().selectLeader(groupId);
            log.info("向leader:{}节点发送rpcClient 请求", leader.getEndpoint().toString());
            this.rpcClient.invokeWithCallback(leader.getEndpoint().toString(), request, invokeCallback, RpcContans.rpcTimeoutMillis);
        }


    }

    @Override
    public void deregister(String appName, String instanceId) {

        try {
            RpcRequest rpcRequest = null;
            List<Instance> instacnes = getInstacnes(appName);
            if (CollectionUtils.isEmpty(instacnes)) {
                DeleteRequest request = new DeleteRequest();
                request.setKey(appName + instanceId);
                rpcRequest = request;
            } else {
                instacnes.removeIf(instance -> instance.getInstanceId().equals(instanceId));
                PutRequest request = new PutRequest();
                request.setKey(appName + instanceId);
                request.setValue(JacksonUtils.toJson(instacnes));
                rpcRequest = request;
            }

            rpcInvokeWithCallback(rpcRequest, rpcResponse -> {
                log.info("register rpcResponse:{}", rpcResponse);
                if (rpcResponse.isSuccess()) {
                    log.error("deregister instance filad appName:{},insatcneid:{}", appName, instanceId);
                }
            });

        } catch (Exception e) {
            log.error("deregister instance filad appName:{},insatcneid:{}", appName, instanceId);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CliOptions cliOpts = new CliOptions();
        cliOpts.setTimeoutMs(5000);
        cliOpts.setMaxRetry(0);

        this.cliService = RaftServiceFactory.createAndInitCliService(cliOpts);
        this.cliClientService = ((CliServiceImpl) this.cliService).getCliClientService();
        Assert.notNull(this.cliClientService, "cliClientService must not be null");
        this.rpcClient = ((AbstractBoltClientService) this.cliClientService).getRpcClient();
    }

    @Override
    public void destroy() throws Exception {
        if (this.cliService != null) {
            this.cliService.shutdown();
        }
    }

}

