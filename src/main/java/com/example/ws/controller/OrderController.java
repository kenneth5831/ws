package com.example.ws.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ws.entity.OrderRequest;
import com.example.ws.entity.Order;
import com.example.ws.entity.PageRequestParams;
import com.example.ws.entity.Product;
import com.example.ws.mapper.OrderMapper;
import com.example.ws.mapper.CustomerMapper;
import com.example.ws.mapper.ProductMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private CustomerMapper customerMapper;
    @Resource
    private ProductMapper productMapper;

    // 訂購產品
    @PostMapping
    @Transactional
    public Order createOrder(@RequestBody OrderRequest request) {
        // 驗證會員和產品
        if (customerMapper.existsById(request.getCustomerId()) &&
                productMapper.existsById(request.getProductId())) {
            throw new IllegalArgumentException("Invalid customer or product");
        }

        Product product = productMapper.selectById(request.getProductId());
        if (product.getStock() < request.getQuantity()) {
            throw new IllegalStateException("Insufficient stock");
        }

        // 創建訂單
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
        productMapper.updateById(product); // 樂觀鎖確保庫存更新安全

        return order;
    }

    // 分頁查詢訂單
    @GetMapping("/page")
    public IPage<Order> searchOrders(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Order> pageParam = new PageRequestParams(page, size);
        LambdaQueryWrapper<Order> query = new LambdaQueryWrapper<>();
        if (orderNo != null) {
            query.eq(Order::getOrderNo, orderNo);
        }

        //這段完全不行 要調整  productName + date就會查錯
//        if (productName != null) {
//            query.inSql("product_id IN (SELECT id FROM product WHERE name LIKE CONCAT('%', '%s', '%s')", productName);
//        }
        if (startDate != null && endDate != null) {
            query.between(Order::getPurchaseDate, startDate, endDate);
        }
        return orderMapper.selectPage(pageParam, query);
    }
}