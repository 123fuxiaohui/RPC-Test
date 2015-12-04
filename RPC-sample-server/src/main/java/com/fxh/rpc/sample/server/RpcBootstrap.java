package com.fxh.rpc.sample.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Ruijie on 2015/12/2.
 */
public class RpcBootstrap {
      public static void main(String[] args){
          new ClassPathXmlApplicationContext("spring.xml");
      }
}
