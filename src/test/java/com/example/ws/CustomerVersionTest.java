package com.example.ws;

import com.example.ws.entity.Customer;
import com.example.ws.mapper.CustomerMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CustomerVersionTest {

    @Resource
    private CustomerMapper customerMapper;

    @Test
    public void testOptimisticLocking() {
        // Step 1: 查出兩份相同資料（模擬兩人同時編輯）
        Customer c1 = customerMapper.selectById(1L);
        Customer c2 = customerMapper.selectById(1L);

        // Step 2: 修改並更新第一份資料
        c1.setName("Alice V1");
        customerMapper.updateById(c1);  // 成功，version +1

        // Step 3: 第二份資料嘗試更新（舊 version）
        c2.setName("Alice V2");
        int updated = customerMapper.updateById(c2);  // 更新失敗（version 不符）

        // 驗證
        assert updated == 0 : "應該會更新失敗（樂觀鎖觸發）";
    }
}