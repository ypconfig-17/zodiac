package com.tcsl.zodiac.server.core.jraft.rpc;

import com.alipay.remoting.AsyncContext;
import com.alipay.remoting.BizContext;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.closure.ReadIndexClosure;
import com.alipay.sofa.jraft.util.BytesUtil;
import com.tcsl.zodiac.server.core.jraft.JraftStoreClosure;
import com.tcsl.zodiac.server.core.jraft.JraftClosureAdapter;
import com.tcsl.zodiac.server.core.jraft.ZodiacJraftEngine;
import com.tcsl.zodiac.server.core.model.JraftOperation;
import com.tcsl.zodiac.server.core.model.request.GetRequest;
import com.tcsl.zodiac.server.core.model.response.DeleteResponse;
import com.tcsl.zodiac.server.core.model.response.GetResponse;
import com.tcsl.zodiac.server.core.model.response.RpcResponse;

/**
 * @className: RpcGetProcessor
 * @description:
 * @author: yang pan
 */
public  class RpcGetProcessor extends AbstractRpcProcessor<GetRequest> {


    public RpcGetProcessor(ZodiacJraftEngine jraftServer) {
        super(jraftServer);
    }

    @Override
    public void handleRequest(BizContext bizCtx, AsyncContext asyncCtx, GetRequest request) {
        // 构建应答回调

        GetResponse response = RpcResponse.createGetRsp();
        this.getJraftStroe().get(request.getKey(), new JraftStoreClosure() {
            @Override
            public void run(Status status) {
                if (status.isOk()) {
                    response.setValue((String) getData());
                } else {
                    setFailure(request, response, status, getErrorRsp());
                }
                asyncCtx.sendResponse(response);
            }
        });


    }



    @Override
    public String interest() {
        return GetRequest.class.getName();
    }
}

