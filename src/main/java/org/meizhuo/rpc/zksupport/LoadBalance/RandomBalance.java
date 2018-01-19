package org.meizhuo.rpc.zksupport.LoadBalance;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.meizhuo.rpc.Exception.ProvidersNoFoundException;
import org.meizhuo.rpc.client.RPCRequestNet;
import org.meizhuo.rpc.zksupport.service.ZKClientService;
import org.meizhuo.rpc.zksupport.service.ZKServerService;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by wephone on 18-1-18.
 */
public class RandomBalance implements LoadBalance {

    @Override
    public String chooseIP(String serviceName) throws ProvidersNoFoundException {
        RPCRequestNet.getInstance().serviceLockMap.get(serviceName).readLock().lock();
        Set<String> ipSet=RPCRequestNet.getInstance().serviceNameInfoMap.get(serviceName).getServiceIPSet();
        int ipNum=ipSet.size();
        if (ipNum==0){
            throw new ProvidersNoFoundException();
        }
        RPCRequestNet.getInstance().serviceLockMap.get(serviceName).readLock().unlock();
        Random random = new Random();
        //生成[0,num)区间的整数：
        int index = random.nextInt(ipNum);
        int count = 0;
        for (String ip : ipSet) {
            if (count == index) {
                //返回随机生成的索引位置ip
                return ip;
            }
            count++;
        }
        return null;
    }
}