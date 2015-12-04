package com.ruijie.fxh.registry;

import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by fxh on 2015/12/2.
 * ·þÎñ×¢²áÀà
 */
public class ServiceRegistry {
     private static final Logger LOGGER= LoggerFactory.getLogger(ServiceRegistry.class);
    private CountDownLatch latch=new CountDownLatch(1);
    private String registryAddress;
    public ServiceRegistry(String registryAddress){
        this.registryAddress=registryAddress;
    }
    public void register(String data){
         if(data!=null){
             ZooKeeper zk=connectServer();
             if(zk!=null){
                 createNode(zk,data);
             }
         }
    }
    private ZooKeeper connectServer(){
        ZooKeeper zk=null;
        try {
            zk=new ZooKeeper(this.registryAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {
                public void process(WatchedEvent event) {
                    if(event.getState()==Event.KeeperState.SyncConnected){
                        latch.countDown();
                    }
                }
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zk;
    }
    private  void createNode(ZooKeeper zk,String data){
        try {
            byte[] bytes=data.getBytes();
            String path=zk.create(Constant.ZK_DATA_PATH,bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            LOGGER.debug("create zookeeper node ({} => {})", path, data);
        } catch (KeeperException e) {
            e.printStackTrace();
            LOGGER.error("", e);
        } catch (InterruptedException e) {
            e.printStackTrace();
            LOGGER.error("", e);
        }

    }
}
