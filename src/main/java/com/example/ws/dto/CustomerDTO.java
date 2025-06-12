package com.example.ws.dto;

import com.example.ws.entity.Customer;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "客戶資料 DTO")
public class CustomerDTO {
    @Schema(description = "客戶 ID", example = "1")
    public Long id;

    @Schema(description = "客戶名稱", example = "王小明")
    public String name;

    @Schema(description = "電子郵件", example = "test@example.com")
    public String email;

    @Schema(description = "版本號", example = "1")
    public Integer version;

    public static CustomerDTO from(Customer c) {
        var dto = new CustomerDTO();
        dto.id = c.getId();
        dto.name = c.getName();
        dto.email = c.getEmail();
        dto.version = c.getVersion();
        return dto;
    }

    public Customer toEntity() {
        Customer entity = new Customer();
        entity.setId(id);
        entity.setName(name);
        entity.setEmail(email);
        entity.setVersion(version);
        return entity;
    }
}