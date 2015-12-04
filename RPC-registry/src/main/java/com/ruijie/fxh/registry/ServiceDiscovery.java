package com.ruijie.fxh.registry;


import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Ruijie on 2015/12/2.
 * 服务发现类
 */
public class ServiceDiscovery {
     private static final Logger LOGGER= LoggerFactory.getLogger(ServiceRegistry.class);
    private CountDownLatch latch=new CountDownLatch(1);
    private volatile List<String>  dataList=new ArrayList<String>();
    private String registryAddress;
    public ServiceDiscovery(String registryAddress){
        this.registryAddress=registryAddress;
        ZooKeeper zk=connectServer();
        if(zk!=null){
            watchNode(zk);
        }
    }
    public String Discovery(){
        String data=null;
        int size=dataList.size();
        if(size>0){
            if(size==1){
                data=dataList.get(0);
                LOGGER.debug("using only data: {}", data);
            }else{
                data=dataList.get(ThreadLocalRandom.current().nextInt());
                LOGGER.debug("using random data: {}", data);
            }
        }
        return data;
    }
    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {
                public void process(WatchedEvent event) {
                    if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                        latch.countDown();
                    }
                }
            });
          // latch.await();
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return zk;
    }
    private void watchNode(final ZooKeeper zk){
        System.out.println("ZooKeeper="+zk);
        try {
            List<String> nodeList=zk.getChildren(Constant.ZK_REGISTRY_PATH, new Watcher() {
                public void process(WatchedEvent event) {
                      if(event.getType()==Event.EventType.NodeChildrenChanged){
                          watchNode(zk);
                      }
                }
            });
            System.out.println("nodeList-------"+nodeList);
            List<String> dataList=new ArrayList<String>();
            for(String node:nodeList){
                byte[] bytes=zk.getData(Constant.ZK_REGISTRY_PATH+"/"+node,false,null);
                dataList.add(new String(bytes));
            }
            this.dataList=dataList;
            LOGGER.debug("node data: {}", dataList);
        } catch (KeeperException e) {
            e.printStackTrace();
            LOGGER.debug("node data: {}", dataList);
        } catch (InterruptedException e) {
            e.printStackTrace();
            LOGGER.debug("node data: {}", dataList);
        }
    }
}
