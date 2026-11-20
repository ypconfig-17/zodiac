package com.tcsl.zodiac.server.core.model.request;

import lombok.Data;

/**
 * @className: GetRequest
 * @description:
 * @author: yang pan
 */
@Data
public class GetRequest extends RpcRequest {

    private static final long serialVersionUID = 5761307214441434421L;

    private String  key;

}

