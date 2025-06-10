package com.example.ws.entity;

import lombok.Data;

@Data
public class OrderRequest {
    private Long customerId;
    private Long productId;
    private Integer quantity;
}