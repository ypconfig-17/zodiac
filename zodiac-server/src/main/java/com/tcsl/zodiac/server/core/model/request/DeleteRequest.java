package com.tcsl.zodiac.server.core.model.request;

import lombok.Data;

/**
 * @className: DeleteRequest
 * @description:
 * @author: yang pan
 */
@Data
public class DeleteRequest extends RpcRequest {

    private String  key;

}

