package com.example.ws.dto;

import com.example.ws.entity.Order;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "訂單資料 DTO")
public class OrderDTO {
    @Schema(description = "訂單 ID", example = "10001")
    public Long id;

    @Schema(description = "客戶 ID", example = "1")
    public Long customerId;

    @Schema(description = "產品 ID", example = "2")
    public Long productId;

    @Schema(description = "數量", example = "3")
    public Integer quantity;

    public static OrderDTO from(Order o) {
        var dto = new OrderDTO();
        dto.id = o.getId();
        dto.customerId = o.getCustomerId();
        dto.productId = o.getProductId();
        dto.quantity = o.getQuantity();
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
