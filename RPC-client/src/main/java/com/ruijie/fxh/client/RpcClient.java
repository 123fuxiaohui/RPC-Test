package com.ruijie.fxh.client;

import com.ruijie.fxh.common.RpcDecoder;
import com.ruijie.fxh.common.RpcEncoder;
import com.ruijie.fxh.common.RpcRequest;
import com.ruijie.fxh.common.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fxh on 2015/12/2.
 */
public class RpcClient extends SimpleChannelInboundHandler<RpcResponse>{
    private static final Logger LOGGER= LoggerFactory.getLogger(RpcClient.class);
    private String host;
    private int port;
    private RpcResponse response;
    private final Object obj=new Object();
    public RpcClient(String host,int port){
        this.host=host;
        this.port=port;
    }

    @Override protected void channelRead0(ChannelHandlerContext ctx,
                                    RpcResponse response) throws Exception {
        this.response=response;
        synchronized (obj){
            obj.notifyAll();
        }
    }

    @Override public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                                    throws Exception {
        LOGGER.error("client caught exception", cause);
        ctx.close();
    }
    public RpcResponse send(RpcRequest request) throws InterruptedException {
        EventLoopGroup group=new NioEventLoopGroup();
        Bootstrap bootstrap=new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new RpcEncoder(RpcRequest.class)).addLast(new RpcDecoder(RpcResponse.class)).addLast(RpcClient.this);
            }
        }).option(ChannelOption.SO_KEEPALIVE,true);
        ChannelFuture future=bootstrap.connect(host,port).sync();
        future.channel().writeAndFlush(request).sync();
        synchronized (obj){
            obj.wait();
        }
        if(response!=null){
            future.channel().closeFuture().sync();
        }
        return response;
    }
}
