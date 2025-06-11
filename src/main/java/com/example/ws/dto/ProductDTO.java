package com.example.ws.dto;

import com.example.ws.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "商品資料 DTO")
public class ProductDTO {
    @Schema(description = "商品 ID", example = "1")
    public Long id;

    @Schema(description = "商品名稱", example = "Rice Cooker")
    public String name;

    @Schema(description = "價格", example = "1999")
    public BigDecimal price;

    @Schema(description = "庫存數量", example = "50")
    public Integer stock;

    public static ProductDTO from(Product p) {
        var dto = new ProductDTO();
        dto.id = p.getId();
        dto.name = p.getName();
        dto.price = p.getPrice();
        dto.stock = p.getStock();
        return dto;
    }
    public Product toEntity() {
        Product product = new Product();
        product.setName(this.name);
        product.setPrice(this.price);
        product.setStock(this.stock);
        return product;
    }
}