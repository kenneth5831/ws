package com.example.ws.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ws.dto.ApiResponse;
import com.example.ws.dto.OrderDTO;
import com.example.ws.request.OrderRequest;
import com.example.ws.entity.Order;
import com.example.ws.request.PageRequestParams;
import com.example.ws.entity.Product;
import com.example.ws.mapper.OrderMapper;
import com.example.ws.mapper.CustomerMapper;
import com.example.ws.mapper.ProductMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Tag(name = "Order API", description = "訂單資料管理接口")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Resource
    private OrderMapper orderMapper;

    @Operation(summary = "新增訂單")
    @PostMapping
    public ApiResponse<Order> create(@RequestBody OrderDTO dto) {
        Order order = dto.toEntity();
        orderMapper.insert(order);
        return ApiResponse.ok(order);
    }

    @Operation(summary = "刪除訂單")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        orderMapper.deleteById(id);
        return ApiResponse.ok();
    }

    @Operation(summary = "更新訂單")
    @PutMapping("/{id}")
    public ApiResponse<Order> update(@PathVariable Long id, @RequestBody OrderDTO dto) {
        Order order = dto.toEntity();
        order.setId(id);
        int updated = orderMapper.updateById(order);
        if (updated == 0) throw new RuntimeException("更新失敗，可能是版本不一致");
        return ApiResponse.ok(orderMapper.selectById(id));
    }

    @Operation(summary = "取得訂單")
    @GetMapping("/{id}")
    public ApiResponse<Order> getById(@PathVariable Long id) {
        return ApiResponse.ok(orderMapper.selectById(id));
    }

    @Operation(summary = "分頁查詢訂單")
    @GetMapping
    public ApiResponse<IPage<Order>> getPage(@RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        Page<Order> pageParam = new PageRequestParams(page, size);
        return ApiResponse.ok(orderMapper.selectPage(pageParam, null));
    }
}
