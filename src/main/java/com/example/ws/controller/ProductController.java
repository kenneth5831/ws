package com.example.ws.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ws.dto.ApiResponse;
import com.example.ws.dto.OrderDTO;
import com.example.ws.dto.ProductDTO;
import com.example.ws.entity.Product;
import com.example.ws.mapper.ProductMapper;
import com.example.ws.request.PageRequestParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Product API", description = "商品資料管理接口")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Resource
    private ProductMapper productMapper;

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
        Product product = dto.toEntity();
        product.setId(id);
        int updated = productMapper.updateById(product);
        if (updated == 0) throw new RuntimeException("更新失敗，可能是版本不一致");
        return ApiResponse.ok(productMapper.selectById(id));
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
}