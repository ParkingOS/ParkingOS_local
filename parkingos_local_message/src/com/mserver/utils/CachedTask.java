package com.mserver.utils;

import net.rubyeye.xmemcached.MemcachedClient;

/**
 * 通用缓存任务类，执行一个简单的缓存任务
 */
public abstract class CachedTask<T> {

    private String key;

    private String flag;
    
    private MemcachedClient memcachedClient;
    
    public MemcachedClient getMemcachedClient() {
		return memcachedClient;
	}
    
	public String getKey() {
        return key;
    }
    
    public String getFlag() {
        return flag;
    }

    public CachedTask(String key) {
    	this.key = key;
    }
    
    public CachedTask(String key,String flag) {
        this.key = key;
        this.flag = flag;
    }

    public CachedTask(String key,String flag,MemcachedClient memcachedClient) {
        this.key = key;
        this.flag = flag;
        this.memcachedClient = memcachedClient;
    }
    
    public abstract T run();
}
