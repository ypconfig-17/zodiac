package com.tcsl.zodiac.server.core.serialize;

import java.util.List;

/**
 * @className: Serialiizer
 * @description: 序列化接口
 * @author: yang pan
 * @create: 2021-08-09 11:36
 */
public interface Serializer {



    <T> byte[] serialize(T obj);

    <T> T deserialize(byte[] paramArrayOfByte, Class<T> targetClass);

    <T> byte[] serializeList(List<T> objList);


    <T> List<T> deserializeList(byte[] paramArrayOfByte, Class<T> targetClass) ;

}