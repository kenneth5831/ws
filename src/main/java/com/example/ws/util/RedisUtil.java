package com.example.ws.util;

import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    private final RedissonClient redissonClient;

    public RedisUtil(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 嘗試取得鎖，等待最多3秒，鎖持有timeoutSec秒後自動釋放
     * @param key 鎖的 key
     * @param timeoutSec 鎖的持有時間（秒）
     * @return true 取得鎖成功，false 失敗
     */
    public boolean lock(String key, long timeoutSec) {
        RLock lock = redissonClient.getLock(key);
        try {
            // 等待3秒獲取鎖，鎖超時自動釋放
            return lock.tryLock(3, timeoutSec, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 釋放鎖
     * @param key 鎖的 key
     */
    public void unlock(String key) {
        RLock lock = redissonClient.getLock(key);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    public String getExistingOrderNo(String requestKey){
        RBucket<String> bucket = redissonClient.getBucket(requestKey);
        String existingOrderNo = bucket.get();
        return existingOrderNo;
    }

    public void setRequestBucket(String requestKey, String orderNo) {
        RBucket<String> bucket = redissonClient.getBucket(requestKey);
        bucket.set(orderNo, 1, TimeUnit.HOURS); // 存儲 1 小時
    }

}

