package com.tcsl.zodiac.server.core.jraft;

import com.alipay.sofa.jraft.Closure;
import com.alipay.sofa.jraft.Iterator;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.core.StateMachineAdapter;
import com.alipay.sofa.jraft.error.RaftError;
import com.alipay.sofa.jraft.rhea.errors.StoreCodecException;
import com.alipay.sofa.jraft.rhea.util.StackTraceUtil;
import com.alipay.sofa.jraft.storage.snapshot.SnapshotReader;
import com.alipay.sofa.jraft.storage.snapshot.SnapshotWriter;
import com.tcsl.zodiac.server.core.exception.ExceptionUtil;
import com.tcsl.zodiac.server.core.model.JraftOperation;
import com.tcsl.zodiac.server.core.serialize.Serializers;
import com.tcsl.zodiac.server.core.store.JraftStroe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;


/**
 * @className: LibraSatteMachine
 * @description:
 * @author: yang pan
 */
public class ZodiacSatteMachine extends StateMachineAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(ZodiacSatteMachine.class);

    private AtomicLong leaderTerm = new AtomicLong(-1L);
    private JraftStroe jraftStroe;
    private ZodiacStoreSnapshotFile zodiacStoreSnapshotFile;


    public ZodiacSatteMachine(JraftStroe jraftStroe) {
        this.jraftStroe = jraftStroe;
        zodiacStoreSnapshotFile = ZodiacStoreSnapshotFileFactory.getLibraStoreSnapshotFile(jraftStroe);
    }

    @Override
    public void onApply(Iterator iter) {
        int index = 0;
        int applied = 0;
        JraftClosureAdapter closure = null;

        try {
            while (iter.hasNext()) {
                JraftOperation jraftOp = null;
                // 该节点为leader
                if (iter.done() != null) {
                    closure = (JraftClosureAdapter) iter.done();
                    jraftOp = closure.getOperation();
                } else {
                    final ByteBuffer data = iter.getData();
                    try {
                        jraftOp = Serializers.getDefault().deserialize(data.array(), JraftOperation.class);
                    } catch (Exception e) {
                        LOG.error("反序列化JraftOperation 失败", e);
                        throw e;
                    }
                }

                // 处理
                LOG.info("处理jraft请求 closure value={} jraftOp value={} at logIndex={}", closure, jraftOp, iter.getIndex());
                handlerRequest(jraftOp, closure);
                applied++;
                index++;
                iter.next();


            }
        } catch (Throwable t) {
            index++;
            LOG.error("handlerComposite:{}, 状态机严重错误:", StackTraceUtil.stackTrace(t));
            iter.setErrorAndRollback(index - applied,
                    new Status(RaftError.ESTATEMACHINE, "状态机严重错误: %s.",
                            ExceptionUtil.getStackTrace(t)));
        }

    }

    private void handlerRequest(JraftOperation jraftOp, JraftClosure closure) {
        switch (jraftOp.getOp()) {
            case JraftOperation.PUT:
                jraftStroe.add(jraftOp.getKey(), jraftOp.getValue(), closure);
                break;
            case JraftOperation.DELETE:
                jraftStroe.delete(jraftOp.getKey(), closure);
                break;
            case JraftOperation.GET:
                jraftStroe.get(jraftOp.getKey(), closure);
                break;
        }

    }


    @Override
    public void onSnapshotSave(SnapshotWriter writer, Closure done) {
        zodiacStoreSnapshotFile.onSnapshotSave(writer, done);
    }

    @Override
    public boolean onSnapshotLoad(SnapshotReader reader) {
        if (isLeader()) {
            LOG.warn("leader 无法加载快照");
            return false;
        }

        return zodiacStoreSnapshotFile.onSnapshotLoad(reader);
    }

    public boolean isLeader() {
        return this.leaderTerm.get() > 0;
    }

    @Override
    public void onLeaderStart(final long term) {
        this.leaderTerm.set(term);
        super.onLeaderStart(term);

    }

    @Override
    public void onLeaderStop(final Status status) {
        this.leaderTerm.set(-1);
        super.onLeaderStop(status);
    }
}



