package com.example.ws.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ws.dto.ApiResponse;
import com.example.ws.dto.ProductDTO;
import com.example.ws.entity.Product;
import com.example.ws.mapper.ProductMapper;
import com.example.ws.request.PageRequestParams;
import com.example.ws.util.RedisUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Product API", description = "商品資料管理接口")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Resource
    private ProductMapper productMapper;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Operation(summary = "新增商品")
    @PostMapping
    public ApiResponse<Product> create(@RequestBody ProductDTO dto) {
        Product product = dto.toEntity();
        productMapper.insert(product);
        return ApiResponse.ok(product);
    }

    @Operation(summary = "刪除商品")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        productMapper.deleteById(id);
        return ApiResponse.ok();
    }

    @Operation(summary = "更新商品")
    @PutMapping("/{id}")
    public ApiResponse<Product> update(@PathVariable Long id, @RequestBody ProductDTO dto) {
        String lockKey = "product:lock:" + id;
        if (redisUtil.lock(lockKey, 5)) {// 鎖 5 秒
            return ApiResponse.fail("10003", "系統忙碌中，請稍後再試"); // 鎖取得失敗
        }

        try {
            Product product = dto.toEntity();
            product.setId(id);
            int updated = productMapper.updateById(product);
            if (updated == 0) throw new RuntimeException("更新失敗，可能是版本不一致");
            return ApiResponse.ok(productMapper.selectById(id));
        } finally {
            redisUtil.unlock(lockKey); // 解鎖
        }
    }

    @Operation(summary = "取得商品")
    @GetMapping("/{id}")
    public ApiResponse<Product> getById(@PathVariable Long id) {
        return ApiResponse.ok(productMapper.selectById(id));
    }

    @Operation(summary = "分頁查詢商品")
    @GetMapping
    public ApiResponse<IPage<Product>> getPage(@RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        Page<Product> pageParam = new PageRequestParams(page, size);
        return ApiResponse.ok(productMapper.selectPage(pageParam, null));
    }

    @PutMapping("/lock-test/{id}")
    public ApiResponse<String> testLock(@PathVariable Long id) {
        String lockKey = "product:lock:" + id;
        boolean locked = redisUtil.lock(lockKey, 10);
        if (locked) {
            try {
                // 業務邏輯
                Thread.sleep(3000);
                return ApiResponse.ok("成功獲取鎖並處理 product " + id);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return ApiResponse.error("執行中斷");
            } finally {
                redisUtil.unlock(lockKey);
            }
        } else {
            return ApiResponse.error("目前有其他操作正在進行，請稍後再試");
        }
    }
}