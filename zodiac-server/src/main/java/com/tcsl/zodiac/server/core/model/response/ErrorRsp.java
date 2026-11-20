package com.tcsl.zodiac.server.core.model.response;

/**
 * @className: ErrorRsp
 * @description:
 * @author: yang pan
 */
public enum ErrorRsp {


    UNKNOWN_SERVER_ERROR(-1,"未知服务异常"),
    INVALID_REQUEST(-1,"无效请求"),
    STORAGE_ERROR(-1,"存储异常"),
    LEADER_NOT_AVAILABLE(-1,"Leader不可用"),
    NOT_LEADER(-1,"非Leader节点"),
    NONE(0,""),

    ;


    private Integer code;
    private String message;

    ErrorRsp(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
