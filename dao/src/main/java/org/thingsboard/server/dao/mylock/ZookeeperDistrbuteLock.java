package org.thingsboard.server.dao.mylock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.concurrent.CountDownLatch;

public class ZookeeperDistrbuteLock extends ZookeeperAbstractLock {

	public ZookeeperDistrbuteLock(ZookeeperProperties  zookeeperProperties) {
		super.zkClient = new ZkClient(zookeeperProperties.getZkUrl());
	}

	@Override
    Boolean tryLock(String keyName) {
		try {
			zkClient.createEphemeral(PATH+keyName);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	@Override
	void waitLock(String keyName) {

		// 使用事件监听，获取到节点被删除，
		IZkDataListener iZkDataListener = new IZkDataListener() {
			// 当节点被删除
			public void handleDataDeleted(String dataPath) throws Exception {
				if (countDownLatch != null) {
					// 唤醒
					countDownLatch.countDown();
				}

			}

			// 当节点发生改变
			public void handleDataChange(String dataPath, Object data) throws Exception {

			}
		};

		// 注册节点信息
		zkClient.subscribeDataChanges(PATH+keyName, iZkDataListener);
		if (zkClient.exists(PATH+keyName)) {
			// 创建信号量
			countDownLatch = new CountDownLatch(1);
			try {
				// 等待
				countDownLatch.await();
			} catch (Exception e) {

			}

		}
		// 删除事件通知
		zkClient.unsubscribeDataChanges(PATH+keyName, iZkDataListener);
	}

}
