package com.example.ws.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import jakarta.validation.Valid;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
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

    @Operation(summary = "刪除產品")
    @DeleteMapping("/{id}")
    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        if (!productMapper.existsById(id)) {
            return ApiResponse.fail("100015", "產品不存在，ID：" + id);
        }
        productMapper.deleteById(id);
        return ApiResponse.ok();
    }

    @Operation(summary = "更新產品")
    @PutMapping("/{id}")
    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public ApiResponse<Product> update(@PathVariable Long id, @Valid @RequestBody Product product) {
        if (!productMapper.existsById(id)) {
            return ApiResponse.fail("100015", "產品不存在，ID：" + id);
        }
        if (productMapper.exists(new LambdaQueryWrapper<Product>()
                .eq(Product::getName, product.getName())
                .ne(Product::getId, id))) {
            return ApiResponse.fail("100014", "產品名稱已被其他產品使用");
        }
        product.setId(id);
        int updated = productMapper.updateById(product);
        if (updated == 0) {
            return ApiResponse.fail("100016", "更新失敗，可能是版本不一致");
        }
        return ApiResponse.ok(productMapper.selectById(id));
    }

    @Operation(summary = "取得商品")
    @GetMapping("/{id}")
    @Cacheable(value = "products", key = "#id")
    public Product selectById(Long id) {
        return productMapper.selectById(id);
    }

    @Operation(summary = "分頁查詢商品")
    @GetMapping
    @Cacheable(value = "products", key = "'page_' + #page + '_' + #size")
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