package com.example.ws.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.ws.entity.Customer;
import com.example.ws.entity.PageRequestParams;
import com.example.ws.mapper.CustomerMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Resource
    private CustomerMapper customerMapper;

    // 新增會員
    @PostMapping
    public Customer create(@RequestBody Customer customer) {
        customerMapper.insert(customer);
        return customer;
    }

    // 刪除會員
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        customerMapper.deleteById(id);
    }

    // 修改會員
    @PutMapping("/{id}")
    public Customer update(@PathVariable Long id, @RequestBody Customer customer) {
        customer.setId(id);
        customerMapper.updateById(customer); // 樂觀鎖自動生效
        return customer;
    }

    // 查詢單一會員
    @GetMapping("/{id}")
    public Customer getById(@PathVariable Long id) {
        return customerMapper.selectById(id);
    }

    // 分頁查詢會員
    @GetMapping
    public IPage<Customer> getPage(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        Page<Customer> pageParam = new PageRequestParams(page, size);
        return customerMapper.selectPage(pageParam, null);
    }
}