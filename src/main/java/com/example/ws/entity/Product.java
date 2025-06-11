package com.example.ws.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product extends AuditableEntity{
    @TableId(type = IdType.AUTO)
    @Schema(description = "商品 ID", example = "1001")
    private Long id;

    @Schema(description = "商品名稱", example = "無印良品筆記本")
    private String name;

    @Schema(description = "商品價格", example = "99.99")
    private BigDecimal price;

    @Schema(description = "庫存數量", example = "50")
    private Integer stock;

}