package com.tcsl.zodiac.server.core.jraft;

import com.alipay.sofa.jraft.Closure;
import com.alipay.sofa.jraft.storage.snapshot.SnapshotReader;
import com.alipay.sofa.jraft.storage.snapshot.SnapshotWriter;

/**
 * @program: libra
 * @description:
 * @author: Yang Pan
 * @create: 2021-08-07:15:45
 */
public interface ZodiacStoreSnapshotFile {

    /**定期保存 snapshot
     * 启动或者安装 snapshot 后加载 snapshot
     * @param writer
     * @param done
     */
    void onSnapshotSave(SnapshotWriter writer, Closure done);

    /**
     * 启动或者安装 snapshot 后加载 snapshot
     * @param reader
     */
    boolean onSnapshotLoad(SnapshotReader reader);
}

