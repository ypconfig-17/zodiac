package com.tcsl.zodiac.rpc.serialize;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @className: ProtoStuffSerializer
 * @description:
 * @author: yang pan
 */
public class ProtoStuffSerializer implements Serializer{

    /**
     * 序列化对象
     * @param obj
     * @return
     */
    @Override
    public  <T> byte[] serialize(T obj) {
        if (obj == null) {
            throw new RuntimeException("序列化对象(" + obj + ")!");
        }
        //通过对象的类构建对应的schema；
        Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(obj.getClass());
        //使用LinkedBuffer分配一块默认大小的buffer空间；
        LinkedBuffer buffer = LinkedBuffer.allocate(1024 * 1024);
        byte[] protostuff = null;
        try {
            //使用给定的schema将对象序列化为一个byte数组，并返回。
            protostuff = ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new RuntimeException("序列化(" + obj.getClass() + ")对象(" + obj + ")发生异常!", e);
        } finally {
            buffer.clear();
        }
        return protostuff;
    }

    /**
     * 反序列化对象
     * @param paramArrayOfByte
     * @param targetClass
     * @return
     */
    @Override
    public  <T> T deserialize(byte[] paramArrayOfByte, Class<T> targetClass) {
        if (paramArrayOfByte == null || paramArrayOfByte.length == 0) {
            throw new RuntimeException("反序列化对象发生异常,byte序列为空!");
        }
        T instance = null;
        try {
            //实例化一个类的对象
            instance = targetClass.newInstance();
        } catch (InstantiationException  e1) {
            throw new RuntimeException("反序列化过程中依据类型创建对象失败!", e1);
        } catch(IllegalAccessException e2){
            throw new RuntimeException("反序列化过程中依据类型创建对象失败!", e2);
        }
        //通过对象的类构建对应的schema
        Schema<T> schema = RuntimeSchema.getSchema(targetClass);
        //使用给定的schema将byte数组和对象合并，并返回。
        ProtostuffIOUtil.mergeFrom(paramArrayOfByte, instance, schema);
        return instance;
    }

    /**
     * 序列化列表
     * @param objList
     * @return
     */
    @Override
    public  <T> byte[] serializeList(List<T> objList) {
        if (objList == null || objList.isEmpty()) {
            throw new RuntimeException("序列化对象列表(" + objList + ")参数异常!");
        }
        Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(objList.get(0).getClass());
        LinkedBuffer buffer = LinkedBuffer.allocate(1024 * 1024);
        byte[] protostuff = null;
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            ProtostuffIOUtil.writeListTo(bos, objList, schema, buffer);
            protostuff = bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("序列化对象列表(" + objList + ")发生异常!", e);
        } finally {
            buffer.clear();
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return protostuff;
    }

    /**
     * 反序列化列表
     * @param paramArrayOfByte
     * @param targetClass
     * @return
     */
    @Override
    public  <T> List<T> deserializeList(byte[] paramArrayOfByte, Class<T> targetClass) {
        if (paramArrayOfByte == null || paramArrayOfByte.length == 0) {
            throw new RuntimeException("反序列化对象发生异常,byte序列为空!");
        }

        Schema<T> schema = RuntimeSchema.getSchema(targetClass);
        List<T> result = null;
        try {
            result = ProtostuffIOUtil.parseListFrom(new ByteArrayInputStream(paramArrayOfByte), schema);
        } catch (IOException e) {
            throw new RuntimeException("反序列化对象列表发生异常!", e);
        }
        return result;
    }

}