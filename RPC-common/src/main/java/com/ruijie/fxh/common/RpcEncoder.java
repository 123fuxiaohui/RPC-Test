package com.ruijie.fxh.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by Ruijie on 2015/12/2.
 * ±àÂëÆ÷
 */
public class RpcEncoder extends MessageToByteEncoder{
    public Class<?> genericClass;
    public  RpcEncoder(Class<?> genericClass){
        this.genericClass=genericClass;
    }
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object in, ByteBuf out)
                                    throws Exception {
        if(genericClass.isInstance(in)){
            byte[] data=SerializationUtil.serialize(in);
            out.writeInt(data.length);
            out.writeBytes(data);
        }

    }
}
