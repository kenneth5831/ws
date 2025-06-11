package com.example.ws.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("`order`")
@Schema(description = "訂單資料")
public class Order extends AuditableEntity {
    @TableId(type = IdType.AUTO)
    @Schema(description = "訂單 ID", example = "5001")
    private Long id;

    @Schema(description = "訂單編號", example = "ORD-20250611-0001")
    private String orderNo;

    @Schema(description = "客戶 ID", example = "1")
    private Long customerId;

    @Schema(description = "商品 ID", example = "1001")
    private Long productId;

    @Schema(description = "數量", example = "2")
    private Integer quantity;

    @Schema(description = "總金額", example = "199.98")
    private BigDecimal totalAmount;

    @Schema(description = "購買時間", example = "2025-06-11T13:45:00")
    private LocalDateTime purchaseDate;

}