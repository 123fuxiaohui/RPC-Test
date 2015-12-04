package com.ruijie.fxh.client;

import com.ruijie.fxh.common.RpcRequest;
import com.ruijie.fxh.common.RpcResponse;
import com.ruijie.fxh.registry.ServiceDiscovery;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * Created by Ruijie on 2015/12/2.
 */
public class RpcProxy {
     private String serverAddress;
    private ServiceDiscovery serviceDiscovery;
    public RpcProxy(String serverAddress){
        this.serverAddress=serverAddress;
    }
    public RpcProxy(ServiceDiscovery serviceDiscovery){
        this.serviceDiscovery=serviceDiscovery;
    }
    @SuppressWarnings("unchecked")
    public <T> T create(Class<?> interfaceClass){
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] {interfaceClass},
                                        new InvocationHandler() {
                                            public Object invoke(Object proxy, Method method,Object[] args)throws Throwable {
                                                RpcRequest request=new RpcRequest();
                                                request.setRequestId(UUID.randomUUID().toString());
                                                request.setClassName(method.getDeclaringClass().getName());
                                                request.setMethodName(method.getName());
                                                request.setParameterTypes(method.getParameterTypes());
                                                request.setParameters(args);
                                                if(serviceDiscovery!=null){
                                                    serverAddress=serviceDiscovery.Discovery();
                                                }
                                                System.out.println("serverAddress"+serverAddress);
                                                String[] array=serverAddress.split(":");
                                                String host=array[0];
                                                int port=Integer.parseInt(array[1]);
                                                RpcClient rpcClient=new RpcClient(host,port);
                                                RpcResponse response=rpcClient.send(request);
                                                if(response.isError()){
                                                    throw response.getError();
                                                }else{
                                                    return response.getResult();
                                                }
                                            }
                                        });

    }
}
