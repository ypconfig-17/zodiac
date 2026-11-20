package com.tcsl.zodiac.server.core.jraft;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.tcsl.zodiac.server.core.store.StoreType;
import lombok.Data;

import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @className: JraftConfig
 * @description:
 * @author: yang pan
 */
@Data
public class JraftConfig {

    public static final String DEFAULT_GOURP = "zodiac";
    private String dataPath;
    private String groupId = DEFAULT_GOURP;
    private String serverAddress;
    private String initialServers;
    private StoreType storeType = StoreType.MEMORY;
    private int jraftExecutorThreadPoolSize = 10;
    private final Executor executor;

    public JraftConfig(String dataPath, String groupId, String serverAddress, String initialServers, StoreType storeType, int jraftExecutorThreadPoolSize ) {
        this.dataPath = dataPath;
        this.groupId = groupId;
        this.serverAddress = serverAddress;
        this.initialServers = initialServers;
        this.storeType = storeType;
        this.jraftExecutorThreadPoolSize = jraftExecutorThreadPoolSize;
        executor = new ThreadPoolExecutor(
                1,this.jraftExecutorThreadPoolSize , 0, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new ThreadFactoryBuilder()
                        .setNameFormat("RpcClient-pool-%d")
                        .build());
    }

    public JraftConfig(String dataPath, String groupId, String serverAddress, String initialServers, int jraftExecutorThreadPoolSize ) {
        this(dataPath, groupId, serverAddress, initialServers, StoreType.MEMORY,  jraftExecutorThreadPoolSize );
    }


}
