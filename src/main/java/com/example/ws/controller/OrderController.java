package com.example.ws.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ws.dto.ApiResponse;
import com.example.ws.dto.CustomerDTO;
import com.example.ws.dto.OrderDTO;
import com.example.ws.entity.Order;
import com.example.ws.entity.Product;
import com.example.ws.mapper.CustomerMapper;
import com.example.ws.mapper.ProductMapper;
import com.example.ws.request.PageRequestParams;
import com.example.ws.mapper.OrderMapper;
import com.example.ws.util.OrderUtil;
import com.example.ws.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private RedisUtil redisUtil;

    @Resource
    private ProductMapper productMapper;

    @Resource
    private CustomerMapper customerMapper;

    @Operation(summary = "新增訂單")
    @PostMapping
    @Transactional
    public ApiResponse<OrderDTO> create(@RequestBody OrderDTO dto) {
        Order order = dto.toEntity();

        Product product = productMapper.selectById(order.getProductId());
        if (ObjectUtils.isEmpty(product)) {
            return ApiResponse.fail("100004", "無效的產品編號");
        }

        if (order.getQuantity() <= 0) {
            return ApiResponse.fail("100005", "無效的訂單數量");
        }

        if (!customerMapper.existsById(order.getCustomerId())) {
            return ApiResponse.fail("100006", "無效的用戶");
        }

        // 1. RedisUtil 加鎖
        String lockKey = "product:stock:" + order.getProductId();
        boolean locked = redisUtil.lock(lockKey, 10); // 鎖定10秒
        if (!locked) {
            return ApiResponse.fail("100008", "無法取得產品庫存鎖，請稍後重試");
        }

        try {
            // 2. 查庫存（SELECT）
            product = productMapper.selectById(order.getProductId()); // 重新查詢確保最新數據
            if (product == null) {
                return ApiResponse.fail("100004", "產品不存在");
            }

            // 3. 判斷庫存是否足夠
            if (product.getStock() < order.getQuantity()) {
                return ApiResponse.fail("100009", "庫存不足，剩餘庫存：" + product.getStock());
            }

            // 4. 減庫存並 updateById（MyBatis-Plus 樂觀鎖）
            product.setStock(product.getStock() - order.getQuantity());
            int updated = productMapper.updateById(product);
            if (updated == 0) {
                return ApiResponse.fail("100010", "庫存更新失敗，可能是並發修改");
            }

            // 設置訂單資訊
            order.setOrderNo(orderUtil.generateOrderNumber());
            order.setTotalAmount(product.getPrice().multiply(BigDecimal.valueOf(order.getQuantity())));
            log.info("[訂單號] {}", order.getOrderNo());
            orderMapper.insert(order);
            return ApiResponse.ok(OrderDTO.from(order));
        } finally {
            // 5. RedisUtil 解鎖
            redisUtil.unlock(lockKey);
        }
    }

    @Operation(summary = "刪除訂單")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        orderMapper.deleteById(id);
        return ApiResponse.ok();
    }

    @Operation(summary = "更新訂單")
    @PutMapping("/{id}")
    public ApiResponse<OrderDTO> update(@PathVariable Long id, @RequestBody OrderDTO dto) {
        Order order = dto.toEntity();
        order.setId(id);
        int updated = orderMapper.updateById(order);
        if (updated == 0) throw new RuntimeException("更新失敗，可能是版本不一致");
        return ApiResponse.ok(OrderDTO.from(orderMapper.selectById(id)));
    }

    @Operation(summary = "分頁查詢訂單", description = "根據訂單編號、產品名稱或購買日期範圍進行分頁查詢")
    @GetMapping
    public ApiResponse<IPage<OrderDTO>> getPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = true) long customerId,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Page<Order> pageParam = new PageRequestParams<>(page, size);
        LambdaQueryWrapper<Order> query = new LambdaQueryWrapper<>();

        // 檢查客戶是否存在
        if(!customerMapper.existsById(customerId)){
            return ApiResponse.fail("100006","無效的用戶");
        }

        query.eq(Order::getCustomerId, customerId);

        // 訂單編號查詢 (精確匹配)
        if (StringUtils.isNotBlank(orderNo)) {
            query.eq(Order::getOrderNo, orderNo);
        }

        // 產品名稱查詢 (模糊匹配，通過子查詢)
        if (StringUtils.isNotBlank(productName)) {
            query.inSql(Order::getProductId,
                    "SELECT id FROM product WHERE name LIKE '%" + productName.replace("'", "''") + "%'");
        }

        // 購買日期範圍查詢
        if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDateTime start = LocalDate.parse(startDate, formatter).atStartOfDay();
                LocalDateTime end = LocalDate.parse(endDate, formatter).atTime(23, 59, 59);
                query.between(Order::getPurchaseDate, start, end);
            } catch (Exception e) {
                return ApiResponse.fail("100007", "無效的日期格式，應為 yyyy-MM-dd");
            }
        }

        IPage<Order> orderPage = orderMapper.selectPage(pageParam, query);
        IPage<OrderDTO> dtoPage = orderPage.convert(OrderDTO::from);
        return ApiResponse.ok(dtoPage);
    }
}
