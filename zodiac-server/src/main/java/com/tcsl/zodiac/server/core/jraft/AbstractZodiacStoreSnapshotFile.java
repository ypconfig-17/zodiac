package com.tcsl.zodiac.server.core.jraft;

import com.alipay.sofa.jraft.Closure;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.entity.LocalFileMetaOutter;
import com.alipay.sofa.jraft.error.RaftError;
import com.alipay.sofa.jraft.rhea.serialization.Serializer;
import com.alipay.sofa.jraft.rhea.serialization.Serializers;
import com.alipay.sofa.jraft.rhea.util.StackTraceUtil;
import com.alipay.sofa.jraft.rhea.util.ZipUtil;
import com.alipay.sofa.jraft.storage.snapshot.SnapshotReader;
import com.alipay.sofa.jraft.storage.snapshot.SnapshotWriter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @className: AbstractZodiacStoreSnapshotFile
 * @description:
 * @author: yang pan
 */
public abstract class AbstractZodiacStoreSnapshotFile implements ZodiacStoreSnapshotFile {

    protected final Logger LOG = LoggerFactory.getLogger(AbstractZodiacStoreSnapshotFile.class);

    private static final String SNAPSHOT_DIR = "config";
    private static final String SNAPSHOT_ARCHIVE = "config.zip";
    protected final Serializer serializer    = Serializers.getDefault();
    protected final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();


    @Override
    public void onSnapshotSave(SnapshotWriter writer, Closure done) {
        final String path = writer.getPath();
        Path snapshot = Paths.get(path, SNAPSHOT_DIR);
        try {
            String snapshotPath = snapshot.toString();
            doOnSnapshotSave(snapshotPath);
        } catch (final Throwable t) {
            LOG.error("保存快照失败：{}", StackTraceUtil.stackTrace(t));
            done.run(new Status(RaftError.EIO, "保存快照失败,path:%s,exception:%s", path, t.getMessage()));
        }

    }

    protected abstract void doOnSnapshotSave(String snapshotPath) throws IOException, Exception;

    @Override
    public boolean onSnapshotLoad(SnapshotReader reader) {
        LocalFileMetaOutter.LocalFileMeta meta = (LocalFileMetaOutter.LocalFileMeta) reader.getFileMeta(SNAPSHOT_ARCHIVE);
        String readPath = reader.getPath();

        if (meta == null) {
            LOG.error("无法加载快照文件，path：{}", readPath);
            return false;
        }
        try {
            // 快照路径
            decompressionSnapshot(readPath);
            loadSnapshot(readPath, meta);
            String snapshotPath = Paths.get(readPath + SNAPSHOT_DIR).toString();

            final File tmp = new File(snapshotPath);
            // 删除快照文件,影响解压
            if (tmp.exists()) {
                FileUtils.forceDelete(tmp);
            }
            return true;
        } catch (Throwable t) {
            LOG.error("加载快照失败,path:{},fileList:{},{}", readPath, reader.listFiles(), StackTraceUtil.stackTrace(t));
            return false;
        }
    }

    protected abstract void loadSnapshot(String readPath, LocalFileMetaOutter.LocalFileMeta meta) throws Exception;


    void decompressionSnapshot(String readPath) throws IOException {
        String sourcePath = Paths.get(readPath, SNAPSHOT_ARCHIVE).toString();
        ZipUtil.unzipFile(sourcePath, readPath);
    }
}

