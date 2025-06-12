package com.example.ws;

import com.example.ws.dto.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void testLockSuccess() throws Exception {
        String lockKey = "product:lock:999";
        redisTemplate.delete(lockKey); // 清理之前的鎖
        System.out.println("[TEST] 清除 Redis 鎖: " + lockKey);

        MvcResult result = mockMvc.perform(put("/api/products/lock-test/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(containsString("成功獲取鎖並處理")))
                .andReturn();

        String response = new String(result.getResponse().getContentAsByteArray(), StandardCharsets.UTF_8);
        System.out.println("[TEST] testLockSuccess 回應內容: " + response);
    }

    @Test
    void testLockFail_whenLockAlreadyExists() throws Exception {
        String lockKey = "product:lock:888";
        redisTemplate.opsForValue().set(lockKey, "mock-value"); // 模擬鎖存在
        System.out.println("[TEST] 手動設定 Redis 鎖: " + lockKey);

        MvcResult result = mockMvc.perform(put("/api/products/lock-test/888"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("操作正在進行")))
                .andReturn();

        String response = new String(result.getResponse().getContentAsByteArray(), StandardCharsets.UTF_8);
        System.out.println("[TEST] testLockFail 回應內容: " + response);

        redisTemplate.delete(lockKey); // 測試完畢後清理
        System.out.println("[TEST] 清除 Redis 鎖: " + lockKey);
    }
}