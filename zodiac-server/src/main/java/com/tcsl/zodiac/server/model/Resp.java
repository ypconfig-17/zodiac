package com.tcsl.zodiac.server.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @className: Resp
 * @description:
 * @author: yang pan
 */
@Data
public class Resp <T> implements Serializable {

    private static final long serialVersionUID = -2729810303915846301L;

    private T data;

    private int code;

    private String msg;

    public Resp(T data, String msg, Integer code) {
        this.data = data;
        this.msg = msg;
        this.code = code;
    }

    public  Resp(ErrorCode errorCode) {
        this.msg = errorCode.getMessage();
        this.code = errorCode.getCode();
    }
    public void  setErrorCode(ErrorCode errorCode) {
        this.msg = errorCode.getMessage();
        this.code = errorCode.getCode();
    }
    public static Resp ok(){
        Resp resp = new Resp(ErrorCode.SUCCESS);
        return  resp;
    }

    public static <T>  Resp ok(T data){
        Resp resp = new Resp(ErrorCode.SUCCESS);
        resp.setData(data);
        return  resp;
    }
    public static  Resp fail(){
        Resp resp = new Resp(ErrorCode.FAIL);
        return  resp;
    }

    public static <T>  Resp fail(ErrorCode errorCode){
        Resp resp = new Resp(errorCode);
        return  resp;
    }

    public static  Resp build(ErrorCode code){
        Resp resp = new Resp(code);
        return  resp;
    }

    public static <T> Resp build(T data,ErrorCode code){
        Resp resp = new Resp(code);
        resp.setData(data);
        return  resp;
    }
}
