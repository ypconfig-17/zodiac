package com.tcsl.zodiac.server.core.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: zodiac
 * @description:
 * @author: Yang Pan
 */
@Data
public class JraftOperation implements Serializable {
    private static final long serialVersionUID = 3208866197002571472L;

    private static final String EMPTY_VALUE = null;
    private static final String EMPTY_KEY = null;
    public static final byte PUT =  0x01;
    public static final byte DELETE = 0x02;
    public static final byte MODIFY =  0x03;
    public static final byte GET =  0x04;
    private String key;
    private String value;
    private byte op;

    public JraftOperation() {
    }

    public JraftOperation(String key, String value, byte op) {
        this.key = key;
        this.value = value;
        this.op = op;
    }



    public static JraftOperation createDelete(String key) {
        return new JraftOperation(key,EMPTY_VALUE,DELETE);
    }

    public static JraftOperation createPut(String key,String value) {
        return new JraftOperation(key,value,PUT);

    }
    public static JraftOperation createGet(String key) {
        return new JraftOperation(key,EMPTY_VALUE,GET);
    }

    public static JraftOperation createAllKeys() {
        return new JraftOperation(EMPTY_KEY,EMPTY_VALUE,DELETE);
    }
}

