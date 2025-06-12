package com.example.ws.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ws.dto.ApiResponse;
import com.example.ws.entity.Customer;
import com.example.ws.entity.Order;
import com.example.ws.mapper.CustomerMapper;
import com.example.ws.mapper.OrderMapper;
import com.example.ws.request.PageRequestParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Customer API", description = "會員資料管理接口")
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Resource
    private CustomerMapper customerMapper;

    @Resource
    private OrderMapper orderMapper;

    @Operation(summary = "新增會員")
    @PostMapping
    @Transactional
    @CacheEvict(value = "customers", allEntries = true)
    public ApiResponse<Customer> create(@Valid @RequestBody Customer customer) {
        // 檢查 email 是否已存在
        if (customerMapper.exists(new LambdaQueryWrapper<Customer>().eq(Customer::getEmail, customer.getEmail()))) {
            return ApiResponse.fail("100001", "電子郵件已被使用");
        }
        customerMapper.insert(customer);
        return ApiResponse.ok(customer);
    }

    @Operation(summary = "刪除會員")
    @DeleteMapping("/{id}")
    @Transactional
    @CacheEvict(value = "customers", allEntries = true)
    public ApiResponse<Void> delete(@PathVariable Long id) {
        // 檢查客戶是否存在
        if (!customerMapper.existsById(id)) {
            return ApiResponse.fail("100002", "會員不存在");
        }
        // 檢查是否有未完成訂單
        boolean hasOrders = orderMapper.exists(new LambdaQueryWrapper<Order>().eq(Order::getCustomerId, id));
        if (hasOrders) {
            return ApiResponse.fail("100003", "會員有訂單，無法刪除");
        }
        customerMapper.deleteById(id);
        return ApiResponse.ok();
    }

    @Operation(summary = "更新會員")
    @PutMapping("/{id}")
    @Transactional
    @CacheEvict(value = "customers", allEntries = true)
    public ApiResponse<Customer> update(@PathVariable Long id, @Valid @RequestBody Customer customer) {
        // 檢查客戶是否存在
        if (!customerMapper.existsById(id)) {
            return ApiResponse.fail("100002", "會員不存在");
        }
        // 檢查 email 是否被其他客戶使用
        if (customerMapper.exists(new LambdaQueryWrapper<Customer>()
                .eq(Customer::getEmail, customer.getEmail())
                .ne(Customer::getId, id))) {
            return ApiResponse.fail("100001", "電子郵件已被其他會員使用");
        }
        customer.setId(id);
        int updated = customerMapper.updateById(customer);
        if (updated == 0) {
            return ApiResponse.fail("100004", "更新失敗，可能是版本不一致");
        }
        return ApiResponse.ok(customerMapper.selectById(id));
    }

    @Operation(summary = "取得單一會員")
    @GetMapping("/{id}")
    @Cacheable(value = "customers", key = "#id")
    public ApiResponse<Customer> getById(@PathVariable Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            return ApiResponse.fail("100002", "會員不存在");
        }
        return ApiResponse.ok(customer);
    }

    @Operation(summary = "分頁查詢會員")
    @GetMapping
    @Cacheable(value = "customers", key = "'page_' + #page + '_' + #size")
    public ApiResponse<IPage<Customer>> getPage(
            @Parameter(description = "頁碼") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每頁大小") @RequestParam(defaultValue = "10") long size) {
        Page<Customer> pageParam = new PageRequestParams<>(page, size);
        return ApiResponse.ok(customerMapper.selectPage(pageParam, null));
    }
}