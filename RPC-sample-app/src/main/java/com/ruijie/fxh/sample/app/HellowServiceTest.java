package com.ruijie.fxh.sample.app;

import com.ruijie.fxh.client.RpcProxy;
import com.ruijie.fxh.registry.ServiceDiscovery;
import com.ruijie.fxh.sample.client.HellowService;
import com.ruijie.fxh.sample.client.Person;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by Ruijie on 2015/12/2.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring.xml")
public class HellowServiceTest {
    /*@Test
    public void test1(){
        ServiceDiscovery serviceDiscovery=new ServiceDiscovery("127.0.0.1:2181");
        RpcProxy rpcProxy=new RpcProxy(serviceDiscovery);
        HellowService helloService = rpcProxy.create(HellowService.class);
        String result = helloService.hello("World");
        System.out.println("result="+result);
        Assert.assertEquals("Hello! World", result);
        System.out.println("1111");
    }*/
    @Autowired
    private RpcProxy rpcProxy;

    @Test
    public void helloTest1() {
        HellowService helloService = rpcProxy.create(HellowService.class);
        String result = helloService.hello("World");
        System.out.println("result="+result);
        Assert.assertEquals("Hello! World", result);
    }

    @Test
    public void helloTest2() {
        HellowService helloService = rpcProxy.create(HellowService.class);
        String result = helloService.hello(new Person("Yong", "Huang"));
        System.out.println("result="+result);
        //Assert.assertEquals("Hello! Yong Huang", result);
    }

}
