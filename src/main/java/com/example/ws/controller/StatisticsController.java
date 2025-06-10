package com.example.ws.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ws.entity.Customer;
import com.example.ws.entity.Order;
import com.example.ws.mapper.OrderMapper;
import com.example.ws.mapper.CustomerMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private CustomerMapper customerMapper;

    // 統計訂單數大於 n 的會員
    @GetMapping("/customers_by_orders")
    public List<Customer> getCustomersByOrderCount(@RequestParam int n) {
        List<Long> customerIds = orderMapper.selectList(
                        new LambdaQueryWrapper<Order>()
                                .groupBy(Order::getCustomerId)
                                .having("COUNT(id) > {0}", n)
                ).stream() // Convert List to Stream
                .map(Order::getCustomerId) // Map to customerId
                .collect(Collectors.toList()); // Collect back to List
        return customerMapper.selectBatchIds(customerIds);
    }
}