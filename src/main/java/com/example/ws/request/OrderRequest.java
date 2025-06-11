package com.example.ws.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "訂單請求資料，用於建立訂單")
public class OrderRequest {
    @Schema(description = "會員ID", example = "1001", required = true)
    private Long customerId;

    @Schema(description = "產品ID", example = "2001", required = true)
    private Long productId;

    @Schema(description = "購買數量", example = "3", required = true)
    private Integer quantity;
}