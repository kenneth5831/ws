package com.example.ws.dto;

import com.example.ws.entity.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "訂單資料 DTO")
public class OrderDTO {
    @Schema(description = "訂單 ID", example = "10001")
    public Long id;

    @Schema(description = "客戶 ID", example = "1")
    @NotNull(message = "客戶 ID 不可為空")
    public Long customerId;

    @Schema(description = "產品 ID", example = "2")
    public Long productId;

    @Schema(description = "總金額", example = "2000.5")
    public BigDecimal totalAmount;

    @Schema(description = "數量", example = "3")
    @Min(value = 1, message = "數量必須大於 0")
    public Integer quantity;

    @Schema(description = "訂單編號", example = "ORD-20250611-0001")
    public String orderNo;

    @Schema(description = "購買時間", example = "2025-06-11T13:45:00")
    private LocalDateTime purchaseDate;

    public static OrderDTO from(Order o) {
        var dto = new OrderDTO();
        dto.id = o.getId();
        dto.customerId = o.getCustomerId();
        dto.productId = o.getProductId();
        dto.quantity = o.getQuantity();
        dto.totalAmount = o.getTotalAmount();
        dto.orderNo = o.getOrderNo();
        dto.purchaseDate = o.getPurchaseDate();
        return dto;
    }

    public Order toEntity() {
        Order order = new Order();
        order.setCustomerId(this.customerId);
        order.setProductId(this.productId);
        order.setQuantity(this.quantity);
        return order;
    }
}
