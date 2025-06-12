package com.example.ws.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ws.dto.ApiResponse;
import com.example.ws.dto.CustomerDTO;
import com.example.ws.entity.Customer;
import com.example.ws.mapper.CustomerMapper;
import com.example.ws.request.PageRequestParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Customer API", description = "客戶資料管理接口")
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Resource
    private CustomerMapper customerMapper;

    @Operation(summary = "新增客戶", description = "新增一筆新的客戶資料")
    @PostMapping
    public ApiResponse<CustomerDTO> create(@RequestBody CustomerDTO dto) {
        Customer customer = dto.toEntity();
        customerMapper.insert(customer);
        Customer saved = customerMapper.selectById(customer.getId());
        return ApiResponse.ok(CustomerDTO.from(saved));
    }

    @Operation(summary = "刪除客戶", description = "依據客戶 ID 刪除資料")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @Parameter(description = "客戶 ID", example = "1")
            @PathVariable Long id) {
        customerMapper.deleteById(id);
        return ApiResponse.ok();
    }

    @Operation(summary = "更新客戶資料", description = "根據 ID 修改對應客戶資料")
    @PutMapping("/{id}")
    public ApiResponse<CustomerDTO> update(@RequestBody CustomerDTO dto) {
        Customer customer = dto.toEntity();
        if(ObjectUtils.anyNull(customer.getId(),customer.getVersion())){
            return ApiResponse.fail("10003", "更新失敗，必填參數未填");
        }

        int updated = customerMapper.updateById(customer);
        if (updated == 0) {
            return ApiResponse.fail("10001", "更新失敗，可能是版本不一致（樂觀鎖觸發）");
        }

        Customer updatedCustomer = customerMapper.selectById(customer.getId());
        return ApiResponse.ok(CustomerDTO.from(updatedCustomer));
    }

    @Operation(summary = "取得單一客戶", description = "根據 ID 取得客戶資料")
    @GetMapping("/{id}")
    public ApiResponse<CustomerDTO> getById(
            @Parameter(description = "客戶 ID", example = "1")
            @PathVariable Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            return ApiResponse.fail("10002", "找不到該客戶");
        }
        return ApiResponse.ok(CustomerDTO.from(customer));
    }

    @Operation(summary = "分頁查詢客戶", description = "分頁查詢客戶清單")
    @GetMapping
    public ApiResponse<IPage<CustomerDTO>> getPage(
            @Parameter(description = "頁碼（從 1 開始）", example = "1")
            @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每頁筆數", example = "10")
            @RequestParam(defaultValue = "10") int size) {

        Page<Customer> pageParam = new PageRequestParams<>(page, size);
        IPage<Customer> customerPage = customerMapper.selectPage(pageParam, null);

        IPage<CustomerDTO> dtoPage = customerPage.convert(CustomerDTO::from);
        return ApiResponse.ok(dtoPage);
    }
}