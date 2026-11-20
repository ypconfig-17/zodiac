package com.tcsl.zodiac.server.model;

/**
 * @className: ErrorCode
 * @description:
 * @author: yang pan
 */
public enum ErrorCode {

    SUCCESS(0,"成功"),
    FAIL(-1,"失败"),
    PARAMETER_ERROR(400,"参数校验失败"),
    SERVER_ERROR(500,"服务器异常"),
    NOT_LEADER(501,"当前节点非主节点,不支持写入数据"),
    ;


    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private int code;
    private String message ;

}
