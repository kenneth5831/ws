package com.example.ws.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

@Tag(name = "Order API", description = "訂單管理接口")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private CustomerMapper customerMapper;

    @Resource
    private ProductMapper productMapper;

    @Operation(summary = "建立訂單", description = "根據客戶與產品建立新訂單，並更新產品庫存")
    @PostMapping
    @Transactional
    public Order createOrder(@RequestBody OrderRequest request) {
        // 驗證會員與產品存在
        if (!customerMapper.existsById(request.getCustomerId()) ||
                !productMapper.existsById(request.getProductId())) {
            throw new IllegalArgumentException("無效的會員或產品");
        }

        Product product = productMapper.selectById(request.getProductId());
        if (product.getStock() < request.getQuantity()) {
            throw new IllegalStateException("庫存不足");
        }

        // 建立訂單
        Order order = new Order();
        order.setOrderNo(UUID.randomUUID().toString());
        order.setCustomerId(request.getCustomerId());
        order.setProductId(request.getProductId());
        order.setQuantity(request.getQuantity());
        order.setTotalAmount(product.getPrice().multiply(new BigDecimal(request.getQuantity())));
        order.setPurchaseDate(LocalDateTime.now());
        orderMapper.insert(order);

        // 更新庫存
        product.setStock(product.getStock() - request.getQuantity());
        boolean success = productMapper.updateById(product) == 1;
        if (!success) {
            throw new OptimisticLockingFailureException("更新庫存失敗，請稍後重試");
        }

        return order;
    }

    @Operation(summary = "分頁查詢訂單", description = "根據訂單號、產品名稱與日期範圍查詢訂單")
    @GetMapping("/page")
    public IPage<Order> searchOrders(
            @Parameter(description = "訂單編號", example = "f4e98f1b-9d12-4a66-8de5-12345678abcd")
            @RequestParam(required = false) String orderNo,
            @Parameter(description = "產品名稱（模糊查詢）", example = "Rice Cooker")
            @RequestParam(required = false) String productName,
            @Parameter(description = "開始日期", example = "2025-01-01T00:00:00")
            @RequestParam(required = false) LocalDateTime startDate,
            @Parameter(description = "結束日期", example = "2025-12-31T23:59:59")
            @RequestParam(required = false) LocalDateTime endDate,
            @Parameter(description = "頁碼", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每頁筆數", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        Page<Order> pageParam = new PageRequestParams(page, size);
        LambdaQueryWrapper<Order> query = new LambdaQueryWrapper<>();

        if (orderNo != null && !orderNo.isBlank()) {
            query.eq(Order::getOrderNo, orderNo);
        }

        if (startDate != null && endDate != null) {
            query.between(Order::getPurchaseDate, startDate, endDate);
        }

        // 查出符合 product name 的 product_id
        if (productName != null && !productName.isBlank()) {
            query.inSql(Order::getProductId,
                    "SELECT id FROM product WHERE name LIKE CONCAT('%', '" + productName + "', '%')");
        }

        return orderMapper.selectPage(pageParam, query);
    }

    @Operation(summary = "建立訂單", description = " 更新訂單的數量與總金額")
    @PutMapping("/{id}")
    public Order updateOrder(
            @PathVariable Long id,
            @RequestBody Order updatedOrder) {

        Order original = orderMapper.selectById(id);
        if (original == null) {
            throw new IllegalArgumentException("訂單不存在");
        }

        original.setQuantity(updatedOrder.getQuantity());
        original.setTotalAmount(updatedOrder.getTotalAmount());
        original.setVersion(updatedOrder.getVersion()); // 傳入前端帶來的 version

        boolean success = orderMapper.updateById(original) == 1;
        if (!success) {
            throw new OptimisticLockingFailureException("更新訂單失敗，請重新取得資料");
        }

        return original;
    }
}
