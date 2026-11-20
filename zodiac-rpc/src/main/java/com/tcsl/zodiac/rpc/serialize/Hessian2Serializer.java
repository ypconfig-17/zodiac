package com.tcsl.zodiac.rpc.serialize;


import java.util.List;


/**
 * @className: Hessian2Serialization
 * @description:
 * @author: yang pan
 */
public class Hessian2Serializer implements Serializer {


    @Override
    public <T> byte[] serialize(T obj) {
        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] paramArrayOfByte, Class<T> targetClass) {
        return null;
    }

    @Override
    public <T> byte[] serializeList(List<T> objList) {
        return new byte[0];
    }

    @Override
    public <T> List<T> deserializeList(byte[] paramArrayOfByte, Class<T> targetClass) {
        return null;
    }
}