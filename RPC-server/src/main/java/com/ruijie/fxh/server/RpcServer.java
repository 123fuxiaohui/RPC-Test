package com.ruijie.fxh.server;


import com.ruijie.fxh.common.RpcDecoder;
import com.ruijie.fxh.common.RpcEncoder;
import com.ruijie.fxh.common.RpcRequest;
import com.ruijie.fxh.common.RpcResponse;
import com.ruijie.fxh.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ruijie on 2015/12/1.
 */
public class RpcServer  implements ApplicationContextAware,InitializingBean {
    private static  final Logger LOGGER=LoggerFactory.getLogger(RpcServer.class);
    private String serverAddress;
    private ServiceRegistry serviceRegistry;
    private Map<String,Object> handlerMap=new HashMap<String, Object>();
    public RpcServer(String serverAddress){
        this.serverAddress=serverAddress;
    }
    public RpcServer(String serverAddress,ServiceRegistry serviceRegistry){
        this.serverAddress=serverAddress;
        this.serviceRegistry=serviceRegistry;
    }
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        Map<String,Object> serviceBeanMap=ctx.getBeansWithAnnotation(RpcService.class);
        if(MapUtils.isNotEmpty(serviceBeanMap)){
            for(Object serviceBean:serviceBeanMap.values()){
                String interfaceName=serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
                handlerMap.put(interfaceName,serviceBean);
            }
        }
    }

    public void afterPropertiesSet() throws Exception {
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup workerGroup=new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap=new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class).childHandler(
                                            new ChannelInitializer<SocketChannel>() {
                                                protected void initChannel(SocketChannel socketChannel)
                                                                                throws Exception {
                                                    socketChannel.pipeline().addLast(new RpcDecoder(RpcRequest.class))
                                                                                    .addLast(new RpcEncoder(RpcResponse.class))
                                                                                    .addLast(new RpcHandler(handlerMap));
                                                }
                                            }).option(ChannelOption.SO_BACKLOG,128).childOption(ChannelOption.SO_KEEPALIVE,true);
            String[] array=serverAddress.split(":");
            String host=array[0];
            int port=Integer.parseInt(array[1]);
            ChannelFuture future=bootstrap.bind(host,port).sync();
            LOGGER.debug("server started on port {}", port);
            if(serviceRegistry!=null){
                serviceRegistry.register(serverAddress);
            }
            future.channel().closeFuture().sync();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
