package com.example.ws.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

import java.math.BigDecimal;

@Data
@TableName("product")
public class Product extends AuditableEntity{
    @TableId(type = IdType.AUTO)
    @Schema(description = "商品 ID", example = "1001")
    private Long id;

    @Schema(description = "商品名稱", example = "無印良品筆記本")
    @NotBlank
    private String name;

    @Schema(description = "商品價格", example = "99.99")
    @NotBlank
    private BigDecimal price;

    @Schema(description = "庫存數量", example = "50")
    @NotBlank
    private Integer stock;

}