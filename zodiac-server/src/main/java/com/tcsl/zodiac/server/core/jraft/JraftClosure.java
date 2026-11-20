package com.tcsl.zodiac.server.core.jraft;

import com.alipay.sofa.jraft.Closure;
import com.tcsl.zodiac.server.core.model.response.ErrorRsp;
import lombok.Data;


/**
 * @className: ZodiacJraftClosure
 * @description:
 * @author: yang pan
 */
public  interface JraftClosure extends Closure {

    ErrorRsp getErrorRsp();

    void setErrorRsp(ErrorRsp error);

    Object getData();

    void setData(Object data);



}

