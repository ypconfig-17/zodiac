package com.tcsl.zodiac.server.core.jraft.rpc;

import com.alipay.remoting.rpc.protocol.AsyncUserProcessor;
import com.alipay.sofa.jraft.Node;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.entity.Task;
import com.alipay.sofa.jraft.error.RaftError;
import com.alipay.sofa.jraft.rhea.serialization.Serializers;
import com.tcsl.zodiac.server.core.jraft.JraftStoreClosure;
import com.tcsl.zodiac.server.core.jraft.JraftClosureAdapter;
import com.tcsl.zodiac.server.core.jraft.ZodiacJraftEngine;
import com.tcsl.zodiac.server.core.model.JraftOperation;
import com.tcsl.zodiac.server.core.model.request.RpcRequest;
import com.tcsl.zodiac.server.core.model.response.ErrorRsp;
import com.tcsl.zodiac.server.core.model.response.RpcResponse;
import com.tcsl.zodiac.server.core.store.JraftCilentStroe;
import com.tcsl.zodiac.server.core.store.JraftStroe;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;

/**
 * @className: AbstractRpcProcessor
 * @description:
 * @author: yang pan
 */
@Data
public abstract class AbstractRpcProcessor<T extends RpcRequest> extends AsyncUserProcessor<T> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected ZodiacJraftEngine zodiacJraftEngine;
    private JraftStroe jraftStroe;

    public AbstractRpcProcessor(ZodiacJraftEngine zodiacJraftEngine) {
        this.zodiacJraftEngine = zodiacJraftEngine;
    }

    public JraftStroe getJraftStroe() {
        return this.zodiacJraftEngine.getJraftStroe();
    }

    protected  void setFailure(final RpcRequest request, final RpcResponse response, final Status status,
                               final ErrorRsp error) {
        response.setErrorRsp(error);
        logger.error("Failed to handle: {}, status: {}, error: {}.", request, status, error);
    }
    @Override
    public String interest() {
        return this.getClass().getName();
    }

}
