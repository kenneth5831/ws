package com.example.ws.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@TableName("customer")
@Schema(description = "客戶資料")
public class Customer extends AuditableEntity {
    @TableId(type = IdType.AUTO)
    @Schema(description = "客戶 ID", example = "1")
    private Long id;

    @Schema(description = "客戶名稱", example = "王小明")
    @NotBlank(message = "姓名不可為空")
    private String name;

    @Schema(description = "電子郵件", example = "test@example.com")
    @Email(message = "無效的電子郵件格式")
    private String email;

}