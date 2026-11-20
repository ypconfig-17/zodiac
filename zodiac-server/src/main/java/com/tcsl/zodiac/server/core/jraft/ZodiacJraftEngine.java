package com.tcsl.zodiac.server.core.jraft;

import com.alipay.remoting.rpc.RpcServer;
import com.alipay.sofa.jraft.Node;
import com.alipay.sofa.jraft.RaftGroupService;
import com.alipay.sofa.jraft.entity.PeerId;
import com.alipay.sofa.jraft.option.NodeOptions;
import com.alipay.sofa.jraft.rpc.RaftRpcServerFactory;
import com.tcsl.zodiac.server.core.jraft.rpc.RpcDeleteProcessor;
import com.tcsl.zodiac.server.core.jraft.rpc.RpcGetProcessor;
import com.tcsl.zodiac.server.core.jraft.rpc.RpcPutProcessor;
import com.tcsl.zodiac.server.core.store.JraftCilentStroe;
import com.tcsl.zodiac.server.core.store.JraftStroe;
import com.tcsl.zodiac.server.core.store.JraftStroeFactory;
import com.tcsl.zodiac.server.core.store.MemoryDBStroe;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @program: libra
 * @description: 配置中心服务
 * @author: Yang Pan
 * @create: 2021-08-08:10:05
 */
@Data
public class ZodiacJraftEngine {
    protected final Logger LOG = LoggerFactory.getLogger(AbstractZodiacStoreSnapshotFile.class);

    private RaftGroupService raftGroupService;
    private volatile long startTime = System.currentTimeMillis();
    private volatile boolean isRunning = false;
    private ZodiacSatteMachine fsm;
    private JraftStroe jraftStroe;
    private JraftStroe jraftMemoryStroe;
    private Node node;
    private JraftConfig jraftConfig;
    private String groupId;

    public ZodiacJraftEngine(JraftConfig jraftConfig) {
        this.jraftConfig = jraftConfig;
    }

    public void init() throws Exception {

        this.jraftMemoryStroe = JraftStroeFactory.createLibraStore(jraftConfig.getStoreType());
        fsm = new ZodiacSatteMachine(jraftMemoryStroe);
        String initialServers = jraftConfig.getInitialServers();
        final String serverAddress = jraftConfig.getServerAddress();
        String raftDataPath = jraftConfig.getDataPath();
        this.groupId = jraftConfig.getGroupId();

        NodeOptions nodeOptions = new NodeOptions();
        // snapshot间隔数 默认一个小时
        nodeOptions.setElectionTimeoutMs(5000);
        nodeOptions.setDisableCli(false);
        nodeOptions.setSnapshotIntervalSecs(30);

        // 解析ip
        PeerId serverId = new PeerId();
        if (!serverId.parse(serverAddress)) {
            throw new IllegalArgumentException("Fail to parse serverId:" + serverAddress);
        }
        // 解析参数
        com.alipay.sofa.jraft.conf.Configuration initConf = new com.alipay.sofa.jraft.conf.Configuration();
        if (!initConf.parse(initialServers)) {
            throw new IllegalArgumentException("Fail to parse initConf:" + initialServers);
        }
        // 设置初始集群配置
        nodeOptions.setInitialConf(initConf);
        nodeOptions.setFsm(fsm);
        // 设置存储路径
        // 日志, 必须
        nodeOptions.setLogUri(raftDataPath + File.separator + "log");
        // 元信息, 必须
        nodeOptions.setRaftMetaUri(raftDataPath + File.separator + "raft_meta");
        // snapshot, 可选, 一般都推荐
        nodeOptions.setSnapshotUri(raftDataPath + File.separator + "snapshot");
        // 初始化文件
        try {
            FileUtils.forceMkdir(new File(raftDataPath));
        } catch (final Throwable t) {
            LOG.error("初始化文件路径失败raftDataPath {}.", raftDataPath);
            throw new IOException("初始化文件路径失败raftDataPath" + raftDataPath);
        }

        final RpcServer rpcServer = new RpcServer(serverId.getPort());
        RaftRpcServerFactory.addRaftRequestProcessors(rpcServer);
        addRpcServerProcessors(rpcServer);
        this.raftGroupService = new RaftGroupService(groupId, serverId, nodeOptions, rpcServer, true);
        this.run();
    }

    private void addRpcServerProcessors(RpcServer rpcServer) {
        rpcServer.registerUserProcessor(new RpcGetProcessor(this));
        rpcServer.registerUserProcessor(new RpcPutProcessor(this));
        rpcServer.registerUserProcessor(new RpcDeleteProcessor(this));
    }

    public void run() {
        if (isRunning) {
            throw new RuntimeException("服务器已经启动运行 ");
        }
        this.node = raftGroupService.start();
        this.jraftStroe = new JraftCilentStroe(this.node, jraftMemoryStroe, jraftConfig.getExecutor());
        this.isRunning = true;
        this.startTime = System.currentTimeMillis();
    }


}

