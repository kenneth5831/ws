package com.example.ws.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        // 為 customers 和 orders 配置緩存（10 分鐘過期）
        Caffeine<Object, Object> defaultCaffeine = Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES);

        // 為 products 配置緩存（10 秒過期）
        Caffeine<Object, Object> productCaffeine = Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.SECONDS);

        // 創建緩存實例
        cacheManager.setCaches(Arrays.asList(
                new CaffeineCache("customers", defaultCaffeine.build()),
                new CaffeineCache("orders", defaultCaffeine.build()),
                new CaffeineCache("products", productCaffeine.build())
        ));

        return cacheManager;
    }
}