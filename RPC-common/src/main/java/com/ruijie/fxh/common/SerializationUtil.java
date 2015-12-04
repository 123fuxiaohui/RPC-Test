package com.ruijie.fxh.common;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Ruijie on 2015/12/2.
 */
public class SerializationUtil {
    private static Map<Class<?>,Schema<?>>  cachedSchema=new ConcurrentHashMap<Class<?>, Schema<?>>();
    private static Objenesis objenesis=new ObjenesisStd(true);
    private SerializationUtil(){

    }
   public static <T> Schema<T> getSchema(Class<T> cls){
       Schema<T> schema= (Schema<T>) cachedSchema.get(cls);
       if(schema==null){
            schema= RuntimeSchema.createFrom(cls);
           if(schema!=null){
               cachedSchema.put(cls,schema);
           }
       }
       return schema;
   }
    /**
     * java对象的序列化对象到字节数组
     */
   public static <T> byte[] serialize(T obj){
        Class<T> cls= (Class<T>) obj.getClass();
       LinkedBuffer buffer=LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
       try {
           Schema<T> schema=getSchema(cls);
           return ProtostuffIOUtil.toByteArray(obj,schema,buffer);
       } catch (Exception e) {
           throw new IllegalStateException(e.getMessage(), e);
       }finally {
           buffer.clear();
       }
   }
    /**
     * java对象的反序列化
     */
    public static <T> T deserialize(byte[] bytes,Class<T> cls){
         T message=objenesis.newInstance(cls);
        Schema<T> schema=getSchema(cls);
        try {
            ProtostuffIOUtil.mergeFrom(bytes, message, schema);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;

    }
}
