package com.tcsl.zodiac.server.core.model.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @className: RpcResponse
 * @description:
 * @author: yang pan
 */
@Data
public abstract class RpcResponse implements Serializable {

    private static final long serialVersionUID = 5761307214441434421L;

    Boolean success = true;
    ErrorRsp errorRsp;

    public Boolean isSuccess() {
        return success;
    }

    public ErrorRsp getErrorRsp() {
        return errorRsp;
    }


    public static DeleteResponse createDeleteRsp() {
        return new DeleteResponse();
    }

    public static GetResponse createGetRsp() {
        return new GetResponse();
    }
    public static PutResponse createPutRsp() {
        return new PutResponse();
    }
    public static RpcResponse errorRsp() {
        return new RpcResponse() {
            private static final long serialVersionUID = -5441207687086957382L;

            @Override
            public void setSuccess(Boolean success) {
                super.setSuccess(false);
                super.setErrorRsp(ErrorRsp.UNKNOWN_SERVER_ERROR);
            }
        };
    }

    public static RpcResponse successRsp() {
        return new RpcResponse() {
            private static final long serialVersionUID = -5441207687086957381L;

            @Override
            public void setSuccess(Boolean success) {
                super.setSuccess(true);
                super.setErrorRsp(ErrorRsp.NONE);
            }
        };
    }
}

