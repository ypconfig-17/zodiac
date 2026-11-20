package com.tcsl.zodiac.server.core.model.request;


import lombok.Data;

/**
 * @className: PutRequest
 * @description:
 * @author: yang pan
 */
@Data
public class PutRequest extends RpcRequest {

    private static final long serialVersionUID = 5761307214441434421L;

    private String key;
    private String value;



}

