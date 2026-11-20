package com.tcsl.zodiac.server.core.jraft;

import com.alipay.sofa.jraft.Closure;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.rhea.errors.Errors;
import com.tcsl.zodiac.server.core.model.JraftOperation;
import com.tcsl.zodiac.server.core.model.request.RpcRequest;
import com.tcsl.zodiac.server.core.model.response.ErrorRsp;
import com.tcsl.zodiac.server.core.model.response.RpcResponse;
import lombok.Data;


/**
 * @className: ZodiacJraftClosure
 * @description:
 * @author: yang pan
 */
@Data
public abstract class JraftStoreClosure implements JraftClosure {

    private volatile ErrorRsp errorRsp;

    private volatile Object data;


}

