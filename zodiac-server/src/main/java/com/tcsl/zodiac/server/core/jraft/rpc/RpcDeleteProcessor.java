package com.tcsl.zodiac.server.core.jraft.rpc;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.sofa.jraft.Status;
import com.tcsl.zodiac.server.core.jraft.JraftStoreClosure;
import com.tcsl.zodiac.server.core.jraft.ZodiacJraftEngine;
import com.tcsl.zodiac.server.core.model.JraftOperation;
import com.tcsl.zodiac.server.core.model.request.DeleteRequest;
import com.tcsl.zodiac.server.core.model.response.DeleteResponse;
import com.tcsl.zodiac.server.core.model.response.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @className: RpcDeleteProcessor
 * @description:
 * @author: yang pan
 */
public class RpcDeleteProcessor extends AbstractRpcProcessor<DeleteRequest> {

    private static final Logger LOG = LoggerFactory.getLogger(RpcDeleteProcessor.class);


    public RpcDeleteProcessor(ZodiacJraftEngine jraftServer) {
        super(jraftServer);
    }

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, DeleteRequest request) {
        // 构建应答回调
        DeleteResponse response = RpcResponse.createDeleteRsp();
        this.getJraftStroe().delete(request.getKey(), new JraftStoreClosure() {
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
        return DeleteRequest.class.getName();
    }
}

