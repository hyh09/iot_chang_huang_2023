package org.thingsboard.server.dao.mylock;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;

import java.util.concurrent.CountDownLatch;

//重构重复代码，将重复代码交给子类执行
@Slf4j
public abstract class ZookeeperAbstractLock  implements   Lock {



	// 创建zk连接
	protected ZkClient zkClient;
	protected static final String PATH = "/lock";
	protected CountDownLatch countDownLatch = null;

	public void getLock(String keyName) {
		if (tryLock(keyName)) {
//			System.out.println("###获取锁成功#####");
		} else {
			// 等待
			waitLock(keyName);
			// 重新获取锁
			getLock(keyName);
		}
	}

	// 是否获取锁成功,成功返回true 失败返回fasle
	abstract Boolean tryLock(String keyName);

	// 等待
	abstract void waitLock(String keyName);

	public void unLock() {
		if (zkClient != null) {
			zkClient.close();
		}
	}

}
