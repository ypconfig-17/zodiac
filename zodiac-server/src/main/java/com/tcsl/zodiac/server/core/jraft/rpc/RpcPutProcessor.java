package com.tcsl.zodiac.server.core.jraft.rpc;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.RpcAsyncContext;
import com.alipay.sofa.jraft.Node;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.entity.PeerId;
import com.tcsl.zodiac.server.core.jraft.JraftStoreClosure;
import com.tcsl.zodiac.server.core.jraft.ZodiacJraftEngine;
import com.tcsl.zodiac.server.core.model.JraftOperation;
import com.tcsl.zodiac.server.core.model.request.PutRequest;
import com.tcsl.zodiac.server.core.model.response.DeleteResponse;
import com.tcsl.zodiac.server.core.model.response.PutResponse;
import com.tcsl.zodiac.server.core.model.response.RpcResponse;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @className: RpcPutProcessor
 * @description:
 * @author: yang pan
 */
public class RpcPutProcessor extends AbstractRpcProcessor<PutRequest> {

    private static final Logger logger = LoggerFactory.getLogger(RpcPutProcessor.class);


    public RpcPutProcessor(ZodiacJraftEngine jraftServer) {
        super(jraftServer);
    }

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, PutRequest request) {
        // 构建应答回调
        PutResponse response = RpcResponse.createPutRsp();
        this.getJraftStroe().add(request.getKey(),request.getValue(), new JraftStoreClosure() {
            @Override
            public void run(Status status) {
                if (status.isOk()) {
                    response.setValue((Boolean) getData());
                } else {
                    setFailure(request, response, status, getErrorRsp());
                }

                logger.info("当前节点peerId,{},发送rpc相应答",getZodiacJraftEngine().getNode().getLeaderId().toString());
                asyncCtx.sendResponse(response);

            }
        });
    }



    @Override
    public String interest() {
        return PutRequest.class.getName();
    }
}

