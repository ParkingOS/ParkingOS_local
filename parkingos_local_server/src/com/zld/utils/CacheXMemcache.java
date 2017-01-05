package com.zld.utils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.CASOperation;
import net.rubyeye.xmemcached.Counter;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;

/**
 * XMemcached缓存类
 */
public class CacheXMemcache<T> {
	
	//private final Logger logger = Logger.getLogger(CacheXMemcache.class);
	
	private MemcachedClient memcachedClient;

	/**
	 * 构造函数
	 */
	public CacheXMemcache() {
	}

	/**
	 * set注入
	 */
	public void setMemcachedClient(MemcachedClient memcachedClient) {
		memcachedClient.setConnectTimeout(1000);
		this.memcachedClient = memcachedClient;
	}
	
	/**
	 * 执行一个任务，如果在缓存中有对应的值，那么直接返回，否则执行任务并把输出保存入缓存
	 * @param task 任务
	 * @return 任务返回值
	 */
	public T doCachedTask(CachedTask<T> task) {
		String key = task.getKey();
		String flag = task.getFlag();
		T value = null;
		try {
			value = getCached(key);
			if (value == null) {//第一次创建
				value = task.run();
				setCached(key,value);
			} else if (flag != null) {//更新
				value = task.run();
				putCachedXM(key, value);
			}
		} catch (Exception e) {
			value = task.run();
			//e.printStackTrace();
		}
		return value;
	}

	/**
	 * CAS操作 实现原子更新
	 */
	public void putCachedXM(String key, final Object object) throws TimeoutException,
			InterruptedException, MemcachedException {
		memcachedClient.cas(key, new CASOperation<Object>() {
			public int getMaxTries() {
				return Integer.MAX_VALUE;
			}

			public Object getNewValue(long currentCAS, Object currentValue) {
				return object;
			}
		});
	}

	/**
	 * 得到缓存
	 * @throws MemcachedException 
	 * @throws InterruptedException 
	 * @throws TimeoutException 
	 */
	public T getCached(String key){
		T t = null;
		try {
			t = memcachedClient.get(key);
		} catch (TimeoutException e) {
			//logger.info(e);
		} catch (InterruptedException e) {
			//logger.info(e);
		} catch (MemcachedException e) {
			//logger.info(e);
		}
		return t;
	}
	
	/**
	 * 更新缓存
	 * @throws MemcachedException 
	 * @throws InterruptedException 
	 * @throws TimeoutException 
	 */
	public boolean setCached(String key,Object object){
		boolean isSuccuess = false;
		try {
			 isSuccuess = memcachedClient.set(key,0,object);
		} catch (TimeoutException e) {
			//logger.info(e);
		} catch (InterruptedException e) {
			//logger.info(e);
		} catch (MemcachedException e) {
			//logger.info(e);
		}
		return isSuccuess;
	}
	

	/**
	 * 计数器
	 * @throws MemcachedException 
	 * @throws InterruptedException 
	 * @throws TimeoutException 
	 */
	public Counter getCounter(String key){
		return memcachedClient.getCounter(key,0);
	}
	
	/**
	 * 关闭连接
	 */
	public void shutdown() {
		try {
			memcachedClient.shutdown();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
