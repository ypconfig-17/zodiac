package com.tcsl.zodiac.server.core.jraft;


import com.alipay.sofa.jraft.entity.LocalFileMetaOutter;
import com.tcsl.zodiac.server.core.store.MemoryDBStroe;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.Lock;
import com.alipay.sofa.jraft.util.Bits;

/**
 * @className: MemoryZodiacStoreSnapshotFile
 * @description:
 * @author: yang pan
 */
public class MemoryZodiacStoreSnapshotFile extends AbstractZodiacStoreSnapshotFile {

    private MemoryDBStroe memoryLibraStroe;

    public MemoryZodiacStoreSnapshotFile(MemoryDBStroe memoryLibraStroe) {
        this.memoryLibraStroe = memoryLibraStroe;
    }

    @Override
    protected void doOnSnapshotSave(String snapshotPath) throws Exception {
        final File file = new File(snapshotPath);
        FileUtils.deleteDirectory(file);
        FileUtils.forceMkdir(file);
        ConcurrentMap<String, String> repository = this.memoryLibraStroe.getRepository();
        Map<String, String> data = new HashMap<>();
        data.putAll(repository);
        MemoryDBStroe.defaultDb defaultDb = new MemoryDBStroe.defaultDb(data);
        writeToFile(snapshotPath, "defaultDb",defaultDb);
        writeToFile(snapshotPath, "defaultDbSize",new MemoryDBStroe.TailIndex(data.size()));
        LOG.info("快照存储数据文件size{}",data.size());
    }

    @Override
    protected void loadSnapshot(String snapshotPath, LocalFileMetaOutter.LocalFileMeta meta) throws Exception {
        Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        final File snapshotFile = new File(snapshotPath);

        if (!snapshotFile.exists()){
            LOG.error("快照文件不存在,snapshotPath:{}",snapshotPath);
            return;
        }
        MemoryDBStroe.defaultDb defaultDb = readFromFile(snapshotPath, "defaultDb", MemoryDBStroe.defaultDb.class);
        Map<String, String> data = defaultDb.data();
        ConcurrentMap<String, String> repository = this.memoryLibraStroe.getRepository();
        repository.putAll(data);
    }

    private <T> T readFromFile(final String rootPath, final String fileName, final Class<T> clazz) throws Exception {
        final Path path = Paths.get(rootPath, fileName);
        final File file = path.toFile();
        if (!file.exists()) {
            throw new NoSuchFieldException(path.toString());
        }
        try (final FileInputStream in = new FileInputStream(file);
             final BufferedInputStream bufInput = new BufferedInputStream(in)) {
            final byte[] lenBytes = new byte[4];
            int read = bufInput.read(lenBytes);
            if (read != lenBytes.length) {
                throw new IOException("fail to read snapshot file length, expects " + lenBytes.length
                        + " bytes, but read " + read);
            }
            final int len = Bits.getInt(lenBytes, 0);
            final byte[] bytes = new byte[len];
            read = bufInput.read(bytes);
            if (read != bytes.length) {
                throw new IOException("fail to read snapshot file, expects " + bytes.length + " bytes, but read "
                        + read);
            }
            return this.serializer.readObject(bytes, clazz);
        }
    }

    private <T> void writeToFile(final String rootPath, final String fileName, final MemoryDBStroe.Persistence<T> persist)
            throws Exception {
        final Path path = Paths.get(rootPath, fileName);
        try (final FileOutputStream out = new FileOutputStream(path.toFile());
             final BufferedOutputStream bufOutput = new BufferedOutputStream(out)) {
            final byte[] bytes = this.serializer.writeObject(persist);
            final byte[] lenBytes = new byte[4];
            Bits.putInt(lenBytes, 0, bytes.length);
            bufOutput.write(lenBytes);
            bufOutput.write(bytes);
            bufOutput.flush();
            out.getFD().sync();
        }
    }
}

