package com.fashion.dto.product;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductDetailRequest {
    private Long id;
    private ColorProductDTO color;
    private String size;
    private Integer quantity;
    private BigDecimal cost;
    private BigDecimal price;
    private Integer discount;
    private Boolean active;
}
