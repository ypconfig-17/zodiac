package com.tcsl.zodiac.server.core.store;



import com.alipay.sofa.jraft.Node;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.closure.ReadIndexClosure;
import com.alipay.sofa.jraft.entity.Task;
import com.alipay.sofa.jraft.error.RaftError;
import com.alipay.sofa.jraft.rhea.serialization.Serializers;
import com.alipay.sofa.jraft.util.BytesUtil;
import com.tcsl.zodiac.server.core.jraft.JraftClosureAdapter;
import com.tcsl.zodiac.server.core.jraft.JraftClosure;
import com.tcsl.zodiac.server.core.model.JraftOperation;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * @className: MemoryDBStroe
 * @description:
 * @author: yang pan
 */
@Data

public class JraftCilentStroe implements JraftStroe {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Node node;
    private final JraftStroe jraftStroe;
    private final Executor readIndexExecutor;

    public JraftCilentStroe(Node node, JraftStroe jraftStroe, Executor readIndexExecutor) {
        this.node = node;
        this.jraftStroe = jraftStroe;
        this.readIndexExecutor = readIndexExecutor;
    }

    private boolean isLeader() {
        return this.node.isLeader();
    }
    @Override
    public void get(String key, JraftClosure closure,boolean readOnlySafe) {
        if (!readOnlySafe) {
            jraftStroe.get(key,closure);
            return;
        }
        this.node.readIndex(BytesUtil.EMPTY_BYTES, new ReadIndexClosure() {
            @Override
            public void run(final Status status, final long index, final byte[] reqCtx) {
                if (status.isOk()) {
                    jraftStroe.get(key,closure);
                    return;
                }
                readIndexExecutor.execute(() -> {
                    if (isLeader()) {
                        logger.warn("ReadIndex 读取失败,尝试应用到状态机:{}", status);
                        // If 'read index' read fails, try to applying to the state machine at the leader node
                        applyOperation(JraftOperation.createGet(key), closure);
                    } else {
                        logger.warn("ReadIndex 读取失败: {}.", status);
                        // Client will retry to leader node
                        new JraftClosureAdapter(closure,JraftOperation.createGet(key)).run(status);
                    }
                });
            }
        });

    }

    @Override
    public void get(String key, JraftClosure closure) {
        get(key,closure,false);
    }

    @Override
    public void allKeys(JraftClosure closure, boolean readOnlySafe) {

        if (!readOnlySafe) {
            jraftStroe.allKeys(closure);
            return;
        }
        this.node.readIndex(BytesUtil.EMPTY_BYTES, new ReadIndexClosure() {
            @Override
            public void run(final Status status, final long index, final byte[] reqCtx) {
                if (status.isOk()) {
                    jraftStroe.allKeys(closure);
                    return;
                }
                readIndexExecutor.execute(() -> {
                    if (isLeader()) {
                        logger.warn("ReadIndex 读取失败,尝试应用到状态机:{}", status);
                        // If 'read index' read fails, try to applying to the state machine at the leader node
                        applyOperation(JraftOperation.createAllKeys(), closure);
                    } else {
                        logger.warn("ReadIndex 读取失败: {}.", status);
                        // Client will retry to leader node
                        new JraftClosureAdapter(closure,JraftOperation.createAllKeys()).run(status);
                    }
                });
            }
        });
    }
    public void allKeys(JraftClosure closure) {
        allKeys(closure,false);
    }

    @Override
    public void getAll(JraftClosure closure) {
        getAll(closure,false);
    }
    @Override
    public void getAll(JraftClosure closure,boolean readOnlySafe) {

        if (!readOnlySafe) {
            jraftStroe.getAll(closure);
            return;
        }
        this.node.readIndex(BytesUtil.EMPTY_BYTES, new ReadIndexClosure() {
            @Override
            public void run(final Status status, final long index, final byte[] reqCtx) {
                if (status.isOk()) {
                    jraftStroe.getAll(closure);
                    return;
                }
                readIndexExecutor.execute(() -> {
                    if (isLeader()) {
                        logger.warn("ReadIndex 读取失败,尝试应用到状态机:{}", status);
                        // If 'read index' read fails, try to applying to the state machine at the leader node
                        applyOperation(JraftOperation.createAllKeys(), closure);
                    } else {
                        logger.warn("ReadIndex 读取失败: {}.", status);
                        // Client will retry to leader node
                        new JraftClosureAdapter(closure,JraftOperation.createAllKeys()).run(status);
                    }
                });
            }
        });
    }

    @Override
    public void delete(String key, JraftClosure closure) {
        applyOperation(JraftOperation.createDelete(key),closure);

    }

    @Override
    public void add(String key,String value, JraftClosure closure) {
        applyOperation(JraftOperation.createPut(key,value),closure);

    }



    protected void applyOperation(final JraftOperation operation, final JraftClosure closure ) {
        if (!isLeader()) {
//            closure.run(new Status(RaftError.EPERM, "Not leader"));
            return;
        }
        final Task task = new Task();
        task.setData(ByteBuffer.wrap(Serializers.getDefault().writeObject(operation)));
        task.setDone(new JraftClosureAdapter(closure,operation));
        this.getNode().apply(task);
    }








}

