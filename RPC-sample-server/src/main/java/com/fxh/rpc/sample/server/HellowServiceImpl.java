package com.fxh.rpc.sample.server;

import com.ruijie.fxh.sample.client.HellowService;
import com.ruijie.fxh.sample.client.Person;
import com.ruijie.fxh.server.RpcService;

/**
 * Created by Ruijie on 2015/12/2.
 */
@RpcService(HellowService.class)
public class HellowServiceImpl implements HellowService{
    public String hello(String name) {
        return "Hello! " + name;
    }

    public String hello(Person person) {
        return person.getFirstName();
    }
}
