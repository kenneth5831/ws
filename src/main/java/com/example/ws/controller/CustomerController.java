package com.example.ws.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ws.entity.Customer;
import com.example.ws.request.PageRequestParams;
import com.example.ws.mapper.CustomerMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Customer API", description = "客戶資料管理接口")
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Resource
    private CustomerMapper customerMapper;

    @Operation(summary = "新增客戶", description = "新增一筆新的客戶資料")
    @PostMapping
    public Customer create(@RequestBody Customer customer) {
        customerMapper.insert(customer);
        return customer;
    }

    @Operation(summary = "刪除客戶", description = "依據客戶 ID 刪除資料")
    @DeleteMapping("/{id}")
    public void delete(
            @Parameter(description = "客戶 ID", example = "1")
            @PathVariable Long id) {
        customerMapper.deleteById(id);
    }

    @Operation(summary = "更新客戶資料", description = "根據 ID 修改對應客戶資料")
    @PutMapping("/{id}")
    public Customer update(
            @Parameter(description = "客戶 ID", example = "1")
            @PathVariable Long id,
            @RequestBody Customer customer) {
        customer.setId(id); // 確保 ID 有設定
        int updated = customerMapper.updateById(customer); // 將帶有 version 的物件更新

        if (updated == 0) {
            throw new RuntimeException("更新失敗，可能是版本不一致（樂觀鎖觸發）");
        }

        return customerMapper.selectById(id);
    }

    @Operation(summary = "取得單一客戶", description = "根據 ID 取得客戶資料")
    @GetMapping("/{id}")
    public Customer getById(
            @Parameter(description = "客戶 ID", example = "1")
            @PathVariable Long id) {
        return customerMapper.selectById(id);
    }

    @Operation(summary = "分頁查詢客戶", description = "分頁查詢客戶清單")
    @GetMapping
    public IPage<Customer> getPage(
            @Parameter(description = "頁碼（從 1 開始）", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每頁筆數", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        Page<Customer> pageParam = new PageRequestParams(page, size);
        return customerMapper.selectPage(pageParam, null);
    }
}
