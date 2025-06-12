package com.example.ws.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ws.dto.ApiResponse;
import com.example.ws.dto.OrderDTO;
import com.example.ws.entity.Order;
import com.example.ws.entity.Product;
import com.example.ws.mapper.CustomerMapper;
import com.example.ws.mapper.ProductMapper;
import com.example.ws.request.PageRequestParams;
import com.example.ws.mapper.OrderMapper;
import com.example.ws.util.OrderUtil;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Tag(name = "Order API", description = "訂單資料管理接口")
@RestController
@RequestMapping("/api/orders")
@Slf4j
public class OrderController {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderUtil orderUtil;

    @Resource
    private ProductMapper productMapper;

    @Resource
    private CustomerMapper customerMapper;

    @Operation(summary = "新增訂單")
    @PostMapping
    public ApiResponse<Order> create(@RequestBody OrderDTO dto) {
        Order order = dto.toEntity();

        Product p = productMapper.selectById(order.getProductId());
        if(ObjectUtils.isEmpty(p)){
            return ApiResponse.fail("100004","無效的產品編號");
        }

        if(order.getQuantity()<=0){
            return ApiResponse.fail("100005","無效的訂單數量");
        }

        if(!customerMapper.existsById(order.getCustomerId())){
            return ApiResponse.fail("100006","無效的用戶");
        }

        order.setOrderNo(orderUtil.generateOrderNumber());
        order.setTotalAmount(p.getPrice().multiply(BigDecimal.valueOf(order.getQuantity())));
        log.info("[訂單號] {}", order.getOrderNo());
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
