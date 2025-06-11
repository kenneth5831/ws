package com.example.ws.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.ws.dto.ApiResponse;
import com.example.ws.dto.CustomerDTO;
import com.example.ws.entity.Customer;
import com.example.ws.entity.Order;
import com.example.ws.mapper.CustomerMapper;
import com.example.ws.mapper.OrderMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "統計報表", description = "統計與分析相關接口")
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private CustomerMapper customerMapper;

    @Operation(
            summary = "統計訂單數大於指定數量的會員列表",
            description = "回傳所有訂單數量大於 n 的會員資料")
    @GetMapping("/customers_by_orders")
    public ApiResponse<List<CustomerDTO>> getCustomersByOrderCount(
            @Parameter(description = "訂單數量閾值", required = true, example = "5")
            @RequestParam int n) {

        List<Long> customerIds = orderMapper.selectObjs(
                        new LambdaQueryWrapper<Order>()
                                .select(Order::getCustomerId)
                                .groupBy(Order::getCustomerId)
                                .having("COUNT(id) > {0}", n)
                ).stream()
                .map(obj -> Long.parseLong(obj.toString()))
                .collect(Collectors.toList());

        if (customerIds.isEmpty()) {
            return ApiResponse.ok(Collections.emptyList());
        }

        List<CustomerDTO> result = customerMapper
                .selectBatchIds(customerIds)
                .stream()
                .map(CustomerDTO::from)
                .collect(Collectors.toList());

        return ApiResponse.ok(result);
    }
}